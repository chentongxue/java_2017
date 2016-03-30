package com.game.draco.app.accumulatelogin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.accumulatelogin.config.AccumulateLoginAwardConfig;
import com.game.draco.app.accumulatelogin.config.AccumulateLoginConfig;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.message.item.AccumulateAwardItem;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.response.C2520_AccumulateLoginRespMessage;
import com.game.draco.message.response.C2521_AccumulateLoginAwardReceiveRespMessage;
import com.game.draco.message.response.C2522_AccumulateLoginAwardDetailRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AccumulateLoginAppImpl implements AccumulateLoginApp {

	private static final int DAYS_CYCLE = 30;
	private static final int DEFAULT_CYCLE = -1;
	private static final byte REFRESH_NEED = 1;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, AccumulateLoginAwardConfig> awardConfigMap = Maps.newLinkedHashMap();
	private AccumulateLoginConfig accumulateLoginConfig;

	// 记录当前玩家展示的是第几轮 <roleId,cycle>
	private Map<String, Byte> cycleMap = Maps.newConcurrentMap() ;

	@Override
	public void start() {
		this.loadAccumulateLoginAward();
		this.loadAccumulateLoginConfig();
	}

	private void loadAccumulateLoginConfig() {
		String fileName = XlsSheetNameType.accumulate_login_config.getXlsName();
		String sheetName = XlsSheetNameType.accumulate_login_config
				.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		accumulateLoginConfig = XlsPojoUtil.getEntity(sourceFile, sheetName,
				AccumulateLoginConfig.class);
		if (null == accumulateLoginConfig) {
			Log4jManager.CHECK
					.error("not config the accumulateLoginConfig,file="
							+ sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}

	private void loadAccumulateLoginAward() {
		this.awardConfigMap = this.loadConfigMap(
				XlsSheetNameType.accumulate_login_award,
				AccumulateLoginAwardConfig.class, true);
		this.checkConfigMap(awardConfigMap);
	}

	private void checkConfigMap(
			final Map<String, AccumulateLoginAwardConfig> configMap) {
		for (AccumulateLoginAwardConfig awardConfig : configMap.values()) {
			awardConfig.init();
		}
	}

	/**
	 * 2521
	 */
	@Override
	public Message receiveAccumulateLoginAwards(RoleInstance role, byte day) {
		RoleCount rc = role.getRoleCount();
		int awardReceiveDays = rc.getRoleTimesToInt(CountType.AccumulateLoginAwardReceiveDays);//getAccumulateLoginAwardReceiveDays();
		int loginDays = rc.getRoleTimesToInt(CountType.AccumulateLoginDays);//getAccumulateLoginDays();

		C2521_AccumulateLoginAwardReceiveRespMessage rtMsg = new C2521_AccumulateLoginAwardReceiveRespMessage();

		// 发送奖励
		Result rcvResult = getReceiveAwardsResult(role, awardReceiveDays,
				loginDays);
		if (rcvResult.isSuccess()) {
			int dayInCycle = awardReceiveDays % DAYS_CYCLE;// 0->0 32->2 30->0
			rtMsg.setDayInCycle((byte) dayInCycle);
			awardReceiveDays++;
			GameContext.getCountApp().setAccumulateLoginCount(role,
					awardReceiveDays, loginDays);

			// eg.第30天领奖后，如果已经31天海未登录过，则略过
			// 如果第31天已经登录过，则再次发送新的列表
			if (awardReceiveDays % DAYS_CYCLE == 0
					&& awardReceiveDays < loginDays) {// 满一轮
				rtMsg.setRefresh(REFRESH_NEED);
			}
			// roleCount
			GameContext.getCountApp().setAccumulateLoginCount(role,
					awardReceiveDays, loginDays);
			// 判断红点提示
			if (!this.hasAward(role)) {
				GameContext.getHintApp().hintChange(role, HintType.accumulatelogin, false);
			}
		} else {
			rtMsg.setInfo(rcvResult.getInfo());
		}

		rtMsg.setType(rcvResult.getResult());

		return rtMsg;
	}

	/**
	 * 获得奖励,vip限制钻石游戏币翻倍 背包满了直接提示背包满，不领取奖励。不发邮件
	 * 
	 * @param role
	 * @param awardReceiveDays
	 * @param loginDays
	 * @date 2014-7-22 上午10:42:27
	 */
	private Result getReceiveAwardsResult(RoleInstance role,
			int awardReceiveDays, int loginDays) {
		int rcvFirstAward = awardReceiveDays + 1;// 本次要领取的奖励 30
		int rcvFirstAwardCycle = getDaysInCycle(rcvFirstAward);// 0~30
		// never happen
		if (rcvFirstAward > loginDays) {
			Result rcvResult = new Result();
			rcvResult.setResult(Result.FAIL);
			String info = GameContext.getI18n().messageFormat(
					TextId.NO_ACCUMULATE_LOGIN_AWARD,
					String.valueOf(rcvFirstAwardCycle));
			rcvResult.setInfo(info);
			return rcvResult;
		}
		// key
		String key = getAwardKey(awardReceiveDays);
		AccumulateLoginAwardConfig awardConfig = awardConfigMap.get(key);
		if (awardConfig == null) {
			key = getDefaultAwardKey(awardReceiveDays);
			awardConfig = awardConfigMap.get(key);
		}
		return sendAccumulateLoginAward(role, awardConfig);
	}

	private String getAwardKey(int rcvFirstAward) {
		int cycle = rcvFirstAward / DAYS_CYCLE;
		int day = rcvFirstAward % DAYS_CYCLE;
		String key = cycle + "_" + day;
		return key;
	}

	private String getDefaultAwardKey(int rcvFirstAward) {
		int day = rcvFirstAward % DAYS_CYCLE;
		String key = DEFAULT_CYCLE + "_" + day;
		return key;
	}
	
	/**
	 * 获得属性倍数
	 * @param role
	 * @param awardConfig
	 * @return
	 */
	private int getAttriMult(RoleInstance role,
			AccumulateLoginAwardConfig awardConfig) {
		// 计算vip属性倍数
		byte times = awardConfig.getTimes();
		if(times <=0){
			return 1 ;
		}
		byte vipLevelLimit = awardConfig.getVipLevel();
		byte roleVipLevel = GameContext.getVipApp().getVipLevel(role);
		if (roleVipLevel >= vipLevelLimit) {
			return times ;
		}
		return 1;
	}

	// 钻石，游戏币奖励可以根据VIP双倍
	private Result sendAccumulateLoginAward(RoleInstance role,
			AccumulateLoginAwardConfig awardConfig) {

		Result rcvResult = new Result();
		//物品奖励
		List<GoodsOperateBean> goodsList = awardConfig.getGoodsList();
		if (!Util.isEmpty(goodsList)) {
			rcvResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role,
					goodsList, OutputConsumeType.accumulate_login_reward);
			if (!rcvResult.isSuccess()) {
				rcvResult.setResult(Result.FAIL);
				String info = getText(TextId.ACCUMULATE_LOGIN_AWARD_BAG_FULL);
				rcvResult.setInfo(info);
				return rcvResult;
			}
		}
		//属性奖励
		Map<AttributeType,Integer> attriMap = awardConfig.getAttriMap() ;
		if(Util.isEmpty(attriMap)){
			rcvResult.setResult(Result.SUCCESS);
			return rcvResult;
		}
		int mult = this.getAttriMult(role, awardConfig);
		for(Map.Entry<AttributeType,Integer> entry : attriMap.entrySet()){
			if(entry.getKey().isMoney()){
				GameContext.getUserAttributeApp().changeRoleMoney(role,
						entry.getKey(), OperatorType.Add, entry.getValue()*mult ,
						OutputConsumeType.accumulate_login_reward);
				continue ;
			}
			GameContext.getUserAttributeApp().changeAttribute(role,
					entry.getKey(), OperatorType.Add, entry.getValue()*mult ,
					OutputConsumeType.accumulate_login_reward);
		}
		// 通知用户属性变化
		role.getBehavior().notifyAttribute();
		rcvResult.setResult(Result.SUCCESS);
		return rcvResult;
	}

	/**
	 * @param rcAwardDays
	 *            已经领取多少天的奖励
	 * @param loginDays
	 *            这是连续登陆第几天
	 * @return
	 * @date 2014-7-22 上午10:38:54
	 */
	private List<AccumulateAwardItem> buildAwardList(String roleId,
			int rcAwardDays, int loginDays) {// 32 36
		// int toRcvFirst = rcAwardDays + 1;//首个未领的 33
		// String key = getAwardKey(toRcvFirst);
		return buildAwardList(roleId, awardConfigMap, rcAwardDays, loginDays);
	}

	// 仅仅在欢迎语中使用
	// 32->2 30->30
	private int getDaysInCycle(int days) {
		if (days > 0 && (days % DAYS_CYCLE == 0)) {
			return DAYS_CYCLE;
		}
		return days % DAYS_CYCLE;
	}

	/**
	 * 28 29 000...0011 29天已领，30天需要领 显示000...0001
	 * 
	 * @param awardConfigMap
	 * @param toRcvFirst
	 * @param loginCycleDays
	 * @return
	 * @date 2014-8-2 下午04:21:18
	 */
	private List<AccumulateAwardItem> buildAwardList(String roleId,
			final Map<String, AccumulateLoginAwardConfig> awardConfigMap,
			int rcAwardDays, int loginCycleDays) {
		List<AccumulateAwardItem> awardList = new ArrayList<AccumulateAwardItem>();
		int cycles = rcAwardDays / DAYS_CYCLE;// 33->1
		int daysCount = cycles * DAYS_CYCLE + 1;// 从第一天开始记算 31
		//
		cycleMap.put(roleId, (byte) cycles);

		for (AccumulateLoginAwardConfig awardConfig : awardConfigMap.values()) {
			if (awardConfig.getCycle() != cycles) {
				continue;
			}
			byte times = awardConfig.getTimes();
			byte vipLevel = awardConfig.getVipLevel();
			byte receivedFlag = 0;

			if (daysCount > rcAwardDays && daysCount <= loginCycleDays) {
				receivedFlag = 1;
			} else if (daysCount > loginCycleDays) {
				receivedFlag = 2;
			}
			daysCount++;
			AccumulateAwardItem awardItem = buildAAwardItem(awardConfig, times,
					vipLevel, receivedFlag);
			awardList.add(awardItem);
		}
		if (awardList.size() < DAYS_CYCLE) {
			cycleMap.put(roleId, (byte) -1);
			daysCount = cycles * DAYS_CYCLE + 1;// 从第一天开始记算 31
			for (AccumulateLoginAwardConfig awardConfig : awardConfigMap
					.values()) {
				if (awardConfig.getCycle() != DEFAULT_CYCLE) {
					continue;
				}
				byte times = awardConfig.getTimes();
				byte vipLevel = awardConfig.getVipLevel();
				byte receivedFlag = 0;

				if (daysCount > rcAwardDays && daysCount <= loginCycleDays) {
					receivedFlag = 1;
				} else if (daysCount > loginCycleDays) {
					receivedFlag = 2;
				}

				daysCount++;
				AccumulateAwardItem awardItem = buildAAwardItem(awardConfig,
						times, vipLevel, receivedFlag);
				awardList.add(awardItem);
			}
		}
		return awardList;
	}

	private AccumulateAwardItem buildAAwardItem(
			AccumulateLoginAwardConfig awardConfig, byte times, byte vipLevel,
			byte received) {
		AccumulateAwardItem awardItem = new AccumulateAwardItem();
		if (awardConfig == null) {
			return awardItem;
		}

		awardItem.setAwardInfo(awardConfig.getAwardInfo());
		awardItem.setImageType(awardConfig.getImageType());
		awardItem.setImageId(awardConfig.getImgeId());
		awardItem.setRatio(awardConfig.getRatio());
		awardItem.setReceived(received);
		awardItem.setTimes(times);
		awardItem.setVipLevel(vipLevel);
		return awardItem;
	}

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void stop() {

	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		// 等级限制判断,达到等级且有奖励弹出
		RoleCount rc = role.getRoleCount();
		int awardReceiveDays = rc.getRoleTimesToInt(CountType.AccumulateLoginAwardReceiveDays);//rc.getAccumulateLoginAwardReceiveDays();
		int loginDays = rc.getRoleTimesToInt(CountType.AccumulateLoginDays);//rc.getAccumulateLoginDays();
		Date loginDate = role.getLastLoginTime();
		Date now = new Date();
		// 今天未登录或者当天创建&首次登录的角色
		if (!DateUtil.sameDay(loginDate, now)
				||((0 == loginDays) && DateUtil.sameDay(role.getCreateTime(), now))) {
			loginDays++;
			GameContext.getCountApp().setAccumulateLoginCount(role,
					awardReceiveDays, loginDays);
			return 1;
		}
		return 0;
	}
	
	private boolean canPushUI(RoleInstance role){
		int roleLevel = role.getLevel();
		if(null == accumulateLoginConfig){
			return true ;
		}
		return roleLevel >= accumulateLoginConfig.getRoleLevel() ;
	}
	
	@Override
	public boolean autoPushUI(RoleInstance role){
		if(!this.canPushUI(role) || !this.hasAward(role)){
			return false ;
		}
		GameContext.getMessageCenter().sendSysMsg(role,this.openAccumulateLoginPanel(role));
		return true ;
	}

	@Override
	public Message openAccumulateLoginPanel(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		int awardReceiveDays = rc.getRoleTimesToInt(CountType.AccumulateLoginAwardReceiveDays);//rc.getAccumulateLoginAwardReceiveDays();
		int loginDays = rc.getRoleTimesToInt(CountType.AccumulateLoginDays);//rc.getAccumulateLoginDays();
		C2520_AccumulateLoginRespMessage msg = getAccumulateLoginRewardsMessage(
				role, awardReceiveDays, loginDays);
		return msg;
	}

	/**
	 * 
	 * @param role
	 * @param awardReceiveDays
	 * @param loginDays
	 * @date 2014-8-1 下午02:59:26
	 */
	private C2520_AccumulateLoginRespMessage getAccumulateLoginRewardsMessage(
			RoleInstance role, int awardReceiveDays, int loginDays) {
		C2520_AccumulateLoginRespMessage msg = new C2520_AccumulateLoginRespMessage();

		String welcomeInfo = getFormatWelcomeText(loginDays);
		msg.setInfo(welcomeInfo);
		// byte roleVipLevel = GameContext.getVipApp().getVipLevel(role);
		String roleId = role.getRoleId();
		List<AccumulateAwardItem> awardList = buildAwardList(roleId,
				awardReceiveDays, loginDays);
		msg.setAwardList(awardList);
		return msg;
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	// 31 -> 1
	private String getFormatWelcomeText(int loginDays) {
		// int accDays = getDaysInCycle(loginDays);
		String welcomeInfo = GameContext.getI18n()
				.messageFormat(TextId.ACCUMULATE_LOGIN_WELCOME_INFO,
						String.valueOf(loginDays));
		return welcomeInfo;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		cycleMap.remove(role.getRoleId());
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		cycleMap.remove(roleId);
		return 1;
	}


	@Override
	public Message getAccumulateLoginAwardDetail(
			RoleInstance role, byte day) {
		C2522_AccumulateLoginAwardDetailRespMessage msg = new C2522_AccumulateLoginAwardDetailRespMessage();
		byte cycle = cycleMap.get(role.getRoleId());
		String key = cycle + "_" + day;
		AccumulateLoginAwardConfig awardConfig = awardConfigMap.get(key);
		if(null == awardConfig){
			return msg ;
		}
		//属性
		Map<AttributeType,Integer> attriMap = awardConfig.getAttriMap() ;
		if(!Util.isEmpty(attriMap)){
			List<AttriTypeValueItem> attriList = Lists.newArrayList() ;
			int mult = this.getAttriMult(role, awardConfig);
			for(Map.Entry<AttributeType,Integer> entry : attriMap.entrySet()){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(entry.getKey().getType());
				item.setAttriValue(entry.getValue()*mult);
				attriList.add(item);
			}
			msg.setAttriList(attriList);
		}
		//物品
		if (Util.isEmpty(awardConfig.getGoodsList())) {
			return msg;
		}
		List<GoodsLiteNamedItem> goodsList = new ArrayList<GoodsLiteNamedItem>();
		for (GoodsOperateBean goodsOperateBean : awardConfig.getGoodsList()) {
			// 获取名称
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
					goodsOperateBean.getGoodsId());
			GoodsLiteNamedItem goodsItem = goodsBase.getGoodsLiteNamedItem();

			goodsItem.setBindType(goodsOperateBean.getBindType().getType());
			goodsItem.setNum((short) goodsOperateBean.getGoodsNum());

			goodsList.add(goodsItem);
		}
		msg.setGoodsList(goodsList);
		return msg;
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		if (this.hasAward(role)) {
			Set<HintType> set = new HashSet<HintType>();
			set.add(HintType.accumulatelogin);
			return set;
		}
		return null;
	}

	@Override
	public boolean hasAward(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		int awardreceiveDays = rc.getRoleTimesToInt(CountType.AccumulateLoginAwardReceiveDays);//rc.getAccumulateLoginAwardReceiveDays();
		int loginDays = rc.getRoleTimesToInt(CountType.AccumulateLoginDays);//rc.getAccumulateLoginDays();
		return awardreceiveDays < loginDays;
	}
}
