package com.game.draco.action.internal;

import java.util.Date;

import org.slf4j.Logger;

import platform.message.request.C5901_ChargeNotifyReqMessage;
import platform.message.response.C5901_ChargeNotifyRespMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.charge.ChargeStatus;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.ChargeRecord;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.internal.C0064_RechargehandleInternalMessage;

/**
 * 单用户单线程处理充值具体逻辑
 */
public class RechargehandleInternalAction extends BaseAction<C0064_RechargehandleInternalMessage> {
	private final Logger logger = Log4jManager.CHARGE_MONEY_LOG;
	
	public class ChargeMoneyResult{
		private boolean success = false ;
		private RoleInstance chargeRole ;
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public RoleInstance getChargeRole() {
			return chargeRole;
		}
		public void setChargeRole(RoleInstance chargeRole) {
			this.chargeRole = chargeRole;
		}
	}
	
	@Override
	public Message execute(ActionContext context,
			C0064_RechargehandleInternalMessage message) {
		Message respMsg = this.doExecute(message.getOriginalMessage());
		message.getOriginalSession().write(respMsg);
		return null ;
	}
		
	private Message doExecute(C5901_ChargeNotifyReqMessage reqMsg) {
		//计费中心返回消息
		C5901_ChargeNotifyRespMessage resp = new C5901_ChargeNotifyRespMessage();
		try{
			//获取订单信息
			String roleId = reqMsg.getRoleId();
			String orderId = reqMsg.getOrderId();
			int channelId = reqMsg.getChannelId();
			String channelOrderId = reqMsg.getChannelOrderId();
			int feeValue = reqMsg.getMoney();//人民币（单位：分）
			//响应消息赋值
			resp.setOrderId(orderId);
			resp.setChannelId(channelId);
			resp.setChannelOrderId(channelOrderId);
			resp.setUserId(reqMsg.getUserId());
			resp.setRoleId(roleId);

			if(this.isExist(channelId, channelOrderId)){
				//返回计费中心消息
				//返回成功，否则计费中心会反复通知
				resp.setResult(1);
				return resp;
			}
			
			String showOrderId = channelOrderId ;
			if(GameContext.getChargeApp().isUseMoogameId(channelId)){
				//使用moogame的流水号
				showOrderId = orderId ;
			}
			
			//验证订单
			if(!validateOrder(reqMsg)){
				String txt = this.messageFormat(TextId.FEE_RECHARGE_FAILURE_MAIL_TEXT,
						reqMsg.getStrResult(),showOrderId);
				//邮件
				this.sendMail(roleId,txt);
				//返回计费中心消息
				resp.setResult(0);
				return resp;
			}
			int payGold = 0;//真实充值的元宝数
			//!!! 如果游戏虚拟币大于0，则表示使用虚拟币充值；否则使用人民币充值。
			int gameMoney = reqMsg.getGameMoney();//游戏虚拟币充值
			if(gameMoney > 0){
				payGold = gameMoney;
			}else{
				//将人民币（分）转换成元宝
				payGold = (int) (feeValue * GameContext.getChargeConfig().getFeeRatioFen());
			}
			//充钱
			ChargeMoneyResult result = this.chargeMoney(roleId, payGold, channelId);
			boolean chargeMoneySucces = result.isSuccess();
			//将充值成功的角色信息返回
			RoleInstance chargeRole = result.getChargeRole();
			if(null != chargeRole){
				resp.setRoleId(roleId);
				resp.setRoleName(chargeRole.getRoleName());
				resp.setChannelUid(chargeRole.getChannelUserId());
			}
			
			ChargeStatus chargeStatus = chargeMoneySucces ? ChargeStatus.Success : ChargeStatus.Fail;
			try{
				ChargeRecord record = new ChargeRecord();
				record.setChannelId(channelId);
				record.setChannelOrderId(channelOrderId);
				record.setOrderId(orderId);
				record.setRecordTime(new Date());
				record.setUserId(null != chargeRole?chargeRole.getUserId():"");
				record.setRoleId(roleId);
				record.setFeeValue(feeValue);
				record.setState(chargeStatus.getType());
				record.setPayGold(payGold);
				record.setGameMoney(gameMoney);
				//record.setUserName(reqMsg.getUserName());
				record.setRoleName(reqMsg.getRoleName());
				//打印充值日志
				record.printUserPayLog();
				//如果充值失败，则给计费中心返回充值失败
				if(!chargeMoneySucces){
					resp.setResult(0);
					return resp;
				}
				//入库
				GameContext.getBaseDAO().insert(record);
				//邮件通知充值
				this.sendMail(roleId, payGold, showOrderId);
				//兑换需要
				GameContext.getCountApp().updateRolePay(result.getChargeRole(), payGold);
				//返回
				resp.setResult(chargeStatus.getType());
				return resp;
			} catch (Exception ex){
				this.logger.error("pay exception after charge sucess", ex);
				resp.setResult(0);
				//如果游戏中给玩家添加钱成功则一定返回给计费中心成功
				if(chargeMoneySucces){
					resp.setResult(1);
				}
				resp.setOrderId(reqMsg.getOrderId());
				resp.setChannelId(reqMsg.getChannelId());
				resp.setChannelOrderId(reqMsg.getChannelOrderId());
				return resp;
			}
			
		}catch(Exception e){
			logger.error("pay exception ：",e);
			resp.setResult(0);
			resp.setOrderId(reqMsg.getOrderId());
			resp.setChannelId(reqMsg.getChannelId());
			resp.setChannelOrderId(reqMsg.getChannelOrderId());
			return resp;
		}
	}
	
	//入库
	private RolePayRecord saveRolePay(String roleId, int payGold) {
		RolePayRecord rpRecord = this.getRolePayRecord(roleId);
		boolean flag = true;
		if (null == rpRecord) {
			flag = false;
			rpRecord = new RolePayRecord();
		}
		rpRecord.setTotalMoney(payGold + rpRecord.getTotalMoney());
		rpRecord.addPayGold(payGold);
		rpRecord.setCurrMoney(payGold + rpRecord.getCurrMoney());
		rpRecord.setLastUpTime(new Date());
		rpRecord.setRoleId(roleId);
		if(flag){
			GameContext.getBaseDAO().update(rpRecord);
		}else{
			GameContext.getBaseDAO().insert(rpRecord);
		}
		return rpRecord ;
	}

	/**
	 * 已经存在对应的记录
	 * @param feeSerialNumber
	 * @return
	 */
	private boolean isExist(int channelId, String channelOrderId){
		return null != GameContext.getBaseDAO().selectEntity(ChargeRecord.class, 
				ChargeRecord.CHANNELID, channelId, ChargeRecord.CHANNELORDERID, channelOrderId);
	}
	
	//验证订单是否处理成功
	private boolean validateOrder(C5901_ChargeNotifyReqMessage notify){
		if(notify == null
				|| notify.getResult()!= 1//订单支付失败
				|| notify.getMoney() <= 0){
			return false;
		}
		return true;
	}
	
	/**
	 * 充钱
	 * @param chargeRoleId
	 * @param userId 
	 * @param payGold 充值元宝数
	 * @param chargeChannelId 充值渠道ID
	 * @return
	 */
	private ChargeMoneyResult chargeMoney(String chargeRoleId, int payGold, int payChannelId){
		ChargeMoneyResult result = new ChargeMoneyResult() ;
		try{
			//获得元宝者人物对象
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(chargeRoleId);
			//玩家是否在线
			if(null == role){
				return this.changeOfflineRoleMoney(chargeRoleId, payGold, payChannelId);
			}
			role.setPayUser(true);
			result.setChargeRole(role);
			//充值日志
			GameContext.getStatLogApp().rolePayLog(role, payGold, payChannelId);
			//添加角色的充值总额
			role.setRolePayGold(role.getRolePayGold() + payGold);
			//修改当前帐号充值对像
			role.getRolePayRecord().addCurrMoney(payGold, payGold);
			//money变化日志
			GameContext.getStatLogApp().roleMoneyLog(role, AttributeType.goldMoney, OutputConsumeType.user_prepaid, payGold, "");
			this.printRolePayLog(role);
			role.getBehavior().notifyAttribute();
		}catch(Exception e){
			logger.error(this.getClass().getName() + ".chargeMoney error: ", e);
			return result;
		}
		result.setSuccess(true);
		return result;
	}
	
	private ChargeMoneyResult changeOfflineRoleMoney(String roleId, int payGold, int payChannelId){
		ChargeMoneyResult result = new ChargeMoneyResult();
		try{
			RoleInstance roleDB = GameContext.getBaseDAO().selectEntity(RoleInstance.class, "roleId", roleId);
			if(null == roleDB){
				this.logger.info("changeOfflineUserMoney select role is null,roleId = " + roleId);
				return result ;
			}
			result.setChargeRole(roleDB);
			//添加角色的充值总额
			roleDB.setRolePayGold(roleDB.getRolePayGold() + payGold);
			GameContext.getBaseDAO().update(roleDB);
			//给帐号充值
			RolePayRecord userPayRecord = this.saveRolePay(roleId, payGold);
			//此时角色不在线需要将此UserPayRecord对象赋值
			roleDB.setRolePayRecord(userPayRecord);
			//充值日志
			GameContext.getStatLogApp().rolePayLog(roleDB, payGold, payChannelId);
			this.printRolePayLog(roleDB);
			//money变化日志
			GameContext.getStatLogApp().roleMoneyLog(roleDB, AttributeType.goldMoney, OutputConsumeType.user_prepaid, payGold, "");
		}catch(Exception e){
			logger.error("changeOfflineUserMoney is error：",e);
			return result;
		}
		result.setSuccess(true);
		return result;
	}

	private void printRolePayLog(RoleInstance role){
		//充值日志
		GameContext.getChargeApp().printRolePayLog(role.getRolePayRecord(), "!");
	}
	
	/**
	 * 发送邮件
	 * @param roleId 角色ID
	 * @param goldMoney 充值元宝数
	 * @param channelOrderId 渠道订单号
	 */
	private void sendMail(String roleId, int goldMoney, String channelOrderId) {
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setRoleId(roleId);
			mail.setContent(this.messageFormat(TextId.FEE_RECHARGE_SUCCESS_MAIL_TEXT,
					goldMoney,channelOrderId));
			mail.setTitle(this.getText(TextId.FEE_RECHARGE_SUCCESS));
			mail.setSendRole(MailSendRoleType.Pay.getName());
			GameContext.getMailApp().sendMail(mail);
		} catch (Exception e) {
			logger.error("send mail pay(success) is error, roleId=" + roleId, e);
		}
	}
	
	private void sendMail(String roleID, String mailContent){
		try{
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setRoleId(roleID);
			mail.setContent(mailContent);
			mail.setTitle(MailSendRoleType.Pay.getName());
			mail.setSendRole(MailSendRoleType.Pay.getName());
			GameContext.getMailApp().sendMail(mail);
		}catch(Exception e){
			logger.error("send mail pay is error:",e);
		}
	}
	
	private RolePayRecord getRolePayRecord(String roleId){
		return GameContext.getBaseDAO().selectEntity(RolePayRecord.class, RolePayRecord.ROLE_ID,roleId);
	}
	
}
