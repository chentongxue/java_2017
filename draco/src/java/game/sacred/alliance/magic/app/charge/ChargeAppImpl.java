package sacred.alliance.magic.app.charge;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import com.game.draco.GameContext;
import com.game.draco.app.operate.growfund.domain.RoleGrowFund;
import com.game.draco.app.operate.monthcard.domain.RoleMonthCard;
import com.game.draco.app.operate.payextra.PayExtraType;
import com.game.draco.app.operate.payextra.config.PayExtraRewardConfig;
import com.game.draco.app.operate.payextra.domain.RolePayExtra;
import com.game.draco.message.item.ChargeStageItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C2801_ChargeMoneyListRespMessage;
import com.google.common.collect.Lists;

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
	public boolean isPayOpen() {
		return GameContext.getChargeConfig().isPayOpen() ;
	}

	@Override
	public Message getChargeMoneyListRespMessage(RoleInstance role) {
		try {
			//未开启充值功能
			if(!this.isPayOpen()){
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
			message.setChargeStageList(this.getChargeStageList(role, resp.getMoneyList(), resp.getMerchandiseList(), GameContext.getChargeConfig().getFeeRatioYuan()));
			message.setRatio(GameContext.getChargeConfig().getFeeRatioYuan());
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
	
	private List<ChargeStageItem> getChargeStageList(RoleInstance role, int[] moneyList, String[] merchandiseList, short ratio) {
		boolean flag = false;
		// 是否有渠道额外参数
		if (null != merchandiseList && moneyList.length == merchandiseList.length) {
			flag = true;
		}
		List<ChargeStageItem> list = Lists.newArrayList();
		for (int i = 0; i < moneyList.length; i++) {
			int money = moneyList[i];
			ChargeStageItem item = new ChargeStageItem();
			item.setMoneyValue(money);
			item.setPointValue(money * ratio);
			PayExtraRewardConfig config = GameContext.getPayExtraApp().getPayExtraRewardConfig(money * ratio);
			if (null != config) {
				this.setExtraItem(role, config, item);
				item.setRecommend((byte) config.getRecommend());// 是否推荐
				item.setActiveId(config.getActiveId());// 对应活动Id
			}
			// 渠道额外参数
			if (flag) {
				item.setFeeInfo(merchandiseList[i]);
			}
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 获取档位说明
	 * @param role
	 * @param config
	 * @return
	 */
	private void setExtraItem(RoleInstance role, PayExtraRewardConfig config, ChargeStageItem item) {
		PayExtraType type = PayExtraType.get(config.getExtraType());
		switch (type) {
		case pay_extra:
			RolePayExtra extra = GameContext.getPayExtraApp().getRolePayExtra(role.getRoleId());
			if (null == extra || !extra.isReward(config.getRechargePoint())) {
				item.setExtraDesc(config.getDesc());
				item.setExtraValue(config.getRewardPoint());
			}
			break;
		case month_card:
			RoleMonthCard roleMonthCard = GameContext.getMonthCardApp().getRoleMonthCard(role);
			if (null == roleMonthCard || !roleMonthCard.isEffective()) {
				item.setExtraDesc(config.getDesc());
			}
			break;
		case grow_fund:
			RoleGrowFund roleGrowFund = GameContext.getGrowFundApp().getRoleGrowFund(role.getRoleId());
			if (null == roleGrowFund) {
				item.setExtraDesc(config.getDesc());
			}
			break;
		default:
			break;
		}
	}
	
	/** 查看玩家充值记录 * */
	public ChargeRecord getRoleAllChargeSum(String rolename){
		return GameContext.getRoleDAO().getRoleAllChargeSum(rolename);
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context){
		RolePayRecord upr = getRolePayRecord(role.getRoleId());
		if(null == upr){
			role.getRolePayRecord().setInsert(true);//表明插入库
			this.printRolePayLog(upr, "+");
			return 1;
		}
		role.setRolePayRecord(upr);
		if(upr.isPayUser()){
			role.setPayUser(true);
		}
		this.printRolePayLog(upr, "+");
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		return 0;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
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
