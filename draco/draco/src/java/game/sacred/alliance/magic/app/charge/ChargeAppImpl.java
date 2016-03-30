package sacred.alliance.magic.app.charge;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C2801_ChargeMoneyListRespMessage;

import platform.message.request.C5900_ChargeArgsReqMessage;
import platform.message.response.C5900_ChargeArgsRespMessage;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.dao.impl.ChargeDAOImpl;
import sacred.alliance.magic.domain.ChargeRecord;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class ChargeAppImpl implements ChargeApp{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private ChargeDAOImpl chargeDAO;
	
	public ChargeDAOImpl getChargeDAO() {
		return chargeDAO;
	}

	public void setChargeDAO(ChargeDAOImpl chargeDAO) {
		this.chargeDAO = chargeDAO;
	}

	@Override
	public Message getChargeMoneyListRespMessage(RoleInstance role) {
		try {
			//未开启充值功能
			if(!GameContext.getChargeConfig().isPayOpen()){
				return new C0003_TipNotifyMessage(Status.Sys_Charge_Not_Open.getTips());
			}
			//获取计费中心的HTTP地址
			String url = GameContext.getPlatformConfig().getFeeCenterChargeArgsUrl();
			if(Util.isEmpty(url)){
				return new C0003_TipNotifyMessage(Status.Sys_Charge_Not_Open.getTips());
			}
			int channelId = role.getChannelId();
			C5900_ChargeArgsReqMessage req = new C5900_ChargeArgsReqMessage();
			req.setAppId(GameContext.getAppId());
			req.setServerId(GameContext.getServerId());
			req.setChannelId(channelId);
			req.setUserId(role.getUserId());
			req.setUserName(role.getUserName());
			req.setRoleId(role.getRoleId());
			req.setRoleName(role.getRoleName());
			req.setRefreshToken(role.getChannelRefreshToken());
			C5900_ChargeArgsRespMessage resp = (C5900_ChargeArgsRespMessage) GameContext.getHttpJsonClient().sendMessage(req, url);
			//请求失败，返回提示信息
			if(null == resp || 1 != resp.getResult()){
				return new C0003_TipNotifyMessage(resp.getTips());
			}
			//返回充值面板信息
			C2801_ChargeMoneyListRespMessage message = new C2801_ChargeMoneyListRespMessage();
			message.setMoneyList(resp.getMoneyList());
			message.setRatio(GameContext.getChargeConfig().getFeeRatioYuan());
			message.setDesc(GameContext.getActiveDiscountApp().getChargeDesc());
			message.setOrderId(resp.getOrderId());
			message.setSignInfo(resp.getSignInfo());
			//如果取到token，则替换role上现有的。
			String accessToken = resp.getAccessToken();
			if(!Util.isEmpty(accessToken)){
				role.setChannelAccessToken(accessToken);
			}
			message.setToken(role.getChannelAccessToken());
			message.setCallbackurl(resp.getCallbackUrl());
			message.setChannelUserId(role.getChannelUserId());
			//第一个备用字段存放的是商品编号
			message.setMark(resp.getRemark1());
			return message;
		} catch (Exception e) {
			this.logger.error("ChargeApp.getChargeMoneyListRespMessage error: ", e);
			return new C0003_TipNotifyMessage(Status.SYS_Charge_Args_Null.getTips());
		}
	}
	
	/** 查看玩家充值记录 * */
	public ChargeRecord getRoleAllChargeSum(String rolename){
		return GameContext.getRoleDAO().getRoleAllChargeSum(rolename);
	}
	
	@Override
	public void init(RoleInstance role){
		RolePayRecord upr = getRolePayRecord(role.getRoleId());
		if(null == upr){
			role.getRolePayRecord().setInsert(true);//表明插入库
			this.printRolePayLog(upr, "+");
			return ;
		}
		role.setRolePayRecord(upr);
		if(upr.isPayUser()){
			role.setPayUser(true);
		}
		this.printRolePayLog(upr, "+");
	}
	
	private RolePayRecord getRolePayRecord(String roleId){
		return GameContext.getBaseDAO().selectEntity(RolePayRecord.class, RolePayRecord.ROLE_ID, roleId);
	}
	
	@Override
	public void updateUserGold(RoleInstance role){
		RolePayRecord upr = role.getRolePayRecord();
		if(upr.isInsert()){
			upr.setRoleId(role.getRoleId());
			GameContext.getBaseDAO().insert(upr);
			upr.setInsert(false);
			return ;
		}
		GameContext.getBaseDAO().update(upr);
	}
	
	//打印充值日志（上/下线时）
	@Override
	public void printRolePayLog(RolePayRecord upr,String type){
		if(null == upr){
			return ;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(type);
		sb.append(",");
		sb.append(upr.getRoleId());
		sb.append(",");
		sb.append(upr.getCurrMoney());
		sb.append(",");
		sb.append(upr.getPayGold());
		sb.append(",");
		sb.append(upr.getTotalMoney());
		sb.append(",");
		sb.append(upr.getConsumeMoney());
		sb.append(",");
		if(null != upr.getLastUpTime()){
			sb.append(DateUtil.date2Str(upr.getLastUpTime(), "yyyy-MM-dd HH:mm:ss"));
		}
		Log4jManager.USER_PAY.info(sb.toString());
	}

	@Override
	public int getPayGold(String userId, Date startDate, Date endDate) {
		return GameContext.getBaseDAO().sum(ChargeRecord.class, "userId", userId, "recordTime", startDate, "recordTime", endDate);
	}

	@Override
	public List<ChargeRecord> getUserChargeRecordList(RoleInstance role, int size) {
		if(size <= 0){
			return null;
		}
		return this.chargeDAO.getUserChargeRecord(role.getUserId(), 0, size);
	}
	
	
	@Override
	public boolean isUseMoogameId(int channelId) {
		String str = GameContext.getChargeConfig().getUseMoogameIdChannels();
		if(Util.isEmpty(str)){
			return false ;
		}
		return str.indexOf(Cat.comma + channelId + Cat.comma ) >=0 ;
	}
	
	
	@Override
	public boolean isRecordShowGameMoney(int channelId){
		String str = GameContext.getChargeConfig().getRecordShowGameMoneyChannels();
		if(Util.isEmpty(str)){
			return false ;
		}
		return str.indexOf(Cat.comma + channelId + Cat.comma ) >=0 ;
	}
}
