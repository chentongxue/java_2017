package com.game.draco.app.alchemy;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.alchemy.config.Alchemy;
import com.game.draco.app.alchemy.config.AlchemyConfig;
import com.game.draco.app.alchemy.config.AlchemyOutBreakConfig;
import com.game.draco.app.alchemy.config.AlchemyVipDailyConfig;
import com.game.draco.app.alchemy.config.LevelRewardConfig;
import com.game.draco.app.alchemy.vo.AlchemyResult;
import com.game.draco.message.item.AlchemyItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C1913_AlchemyDisplayRespMessage;
import com.game.draco.message.response.C1914_AlchemyPlayRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AlchemyAppImpl implements AlchemyApp {
	private final static int FULL_CRIT_RATE = 100 ;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private AlchemyConfig alchemyConfig = null;
	/**
	 * 炼金列表：KEY:id,VALUE:Alchemys
	 */
	private Map<String, Alchemy> alchemyMap = Maps.newLinkedHashMap();

	/**
	 * 炼金暴击率列表
	 */
	private Map<String, AlchemyOutBreakConfig> alchemyOutBreakConfigMap = Maps.newHashMap();
	/**
	 * 炼金VIP限制炼金次数
	 */
	private Map<String, AlchemyVipDailyConfig> alchemyVipDailyConfigMap =  Maps.newHashMap();
	/**
	 * 等级奖励数量、映射表
	 */
	private Map<String,LevelRewardConfig> levelRewardConfigMap =  Maps.newHashMap();

	@Override
	public void start() {
		this.loadAlchemy();
		this.loadAlchemyConfig();
		this.loadAlchemyCritConfig();
		this.loadAlchemyLeveReward();
		this.loadAlchemyVipDailyConfig();
		//TODO:对配置进行验证
	}
	
	@Override
	public Message openAlchemyPanel(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		//调用对是否当天进行判断
		rc.resetDay();
		Map<Byte, Integer> alchemyCountMap = this.getRoleCountMap(rc);
		List<AlchemyItem> alchemyList4Messge = Lists.newArrayList();
		try {
			for (Alchemy alchemy : alchemyMap.values()) {
				alchemyList4Messge.add(this.buildAlchemyItem(role, alchemyCountMap, alchemy));
			}
			C1913_AlchemyDisplayRespMessage message = new C1913_AlchemyDisplayRespMessage();
			message.setHeaderTipsStr(this.getHeaderTipsStr(role));
			message.setFooterTipsStr(alchemyConfig.getFooterTipsStr());
			message.setAlchemyList(alchemyList4Messge);
			return message;
		} catch (Exception e) {
			this.logger.error("AlchemysApp.openAlchemyPanel error: ", e);
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.Sys_Error));
		}
	}
	
	@Override
	public Message getAlchemyResult(RoleInstance role, byte rewardType) {
		C1914_AlchemyPlayRespMessage respMsg = new C1914_AlchemyPlayRespMessage();
		try {
			AlchemyResult result = this.doAlcemy(role, rewardType);
			if (!result.isSuccess()) {
				respMsg.setType(RespTypeStatus.FAILURE);
				respMsg.setInfo(result.getInfo());
				return respMsg;
			}
			// 如果暴击广播
			try {
				if (result.isCrit()
						&& !Util.isEmpty(this.alchemyConfig.getCritBroadcast())) {
					String context = MessageFormat.format(
							this.alchemyConfig.getCritBroadcast(),
							role.getRoleName(),
							result.getAttriType().getName(),
							String.valueOf(result.getRewardNum()));
					GameContext.getChatApp()
							.sendSysMessage(ChatSysName.System,
									ChannelType.Publicize_Personal, context,
									null, null);
				}
			} catch (Exception ex) {
				logger.error("", ex);
			}
			respMsg.setOutbreak(result.isCrit() ? (byte) 1 : (byte) 0);
			respMsg.setInfo(MessageFormat.format(this.alchemyConfig
					.getAlchemySucessInfo(), result.getAttriType().getName(),
					String.valueOf(result.getRewardNum())));
			respMsg.setHeaderTipsStr(this.getHeaderTipsStr(role));
			respMsg.setAlchemyItem(this.buildAlchemyItem(role,
					result.getAlchemyCountMap(), result.getAlchemy()));
			respMsg.setType(RespTypeStatus.SUCCESS);
		} catch (Exception e) {
			this.logger.error("AlchemysApp.getAlchemyResult error: ", e);
			respMsg.setType(RespTypeStatus.FAILURE);
			respMsg.setInfo(this.getText(TextId.Sys_Error));
		}
		return respMsg ;
	}
	
	//要检查越界
	private AlchemyOutBreakConfig getAlchemyOutBreakConfig(int count){
		if(Util.isEmpty(alchemyOutBreakConfigMap)){
			return null ;
		}
		if(count>alchemyOutBreakConfigMap.size()){
			count = alchemyOutBreakConfigMap.size();
		}
		String key = String.valueOf(count);
		return this.alchemyOutBreakConfigMap.get(key);
	}
	
	private LevelRewardConfig getLevelRewardConfig(int roleLevel,int rewardType){
		if(Util.isEmpty(levelRewardConfigMap)){
			return null ;
		}
		String key = roleLevel + Cat.underline + rewardType ;
		return this.levelRewardConfigMap.get(key);
	}
	
	private AlchemyVipDailyConfig getAlchemyVipDailyConfig(int vipLevel,int rewardType){
		if(Util.isEmpty(alchemyVipDailyConfigMap)){
			return null ;
		}
		String key = vipLevel + Cat.underline + rewardType ;
		return this.alchemyVipDailyConfigMap.get(key);
	}
	
	private void loadAlchemyConfig(){
		String fileName = XlsSheetNameType.AlchemyConfig.getXlsName();
		String sheetName = XlsSheetNameType.AlchemyConfig.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		alchemyConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, AlchemyConfig.class);
		if(null == alchemyConfig){
			Log4jManager.CHECK.error("not config the alchemyConfig,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}
	
	private void loadAlchemy() {
		this.alchemyMap = this.loadConfigMap(XlsSheetNameType.Alchemy,
				Alchemy.class, true);
	}

	private void loadAlchemyCritConfig() {
		this.alchemyOutBreakConfigMap = this.loadConfigMap(
				XlsSheetNameType.AlchemyOutBreakConfig,
				AlchemyOutBreakConfig.class, false);
	}
	
	private void loadAlchemyVipDailyConfig(){
		this.alchemyVipDailyConfigMap = this.loadConfigMap(
				XlsSheetNameType.AlchemyVipDailyConfig,
				AlchemyVipDailyConfig.class, false);
	}
	
	private void loadAlchemyLeveReward(){
		this.levelRewardConfigMap = this.loadConfigMap(
				XlsSheetNameType.AlchemyLeveReward,
				LevelRewardConfig.class, false);
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
	public void setArgs(Object arg0) {
		
	}
	

	@Override
	public void stop() {
	}
	
	private Alchemy getAlchemy(int rewardType){
		if(null == this.alchemyMap){
			return null ;
		}
		return this.alchemyMap.get(String.valueOf(rewardType));
	}
	
	private int getHasAlchemyTimes(Map<Byte, Integer> alchemyCountMap,
			byte rewardType){
		Integer t = alchemyCountMap.get(rewardType);
		if (t == null) {
			return 0 ;
		}
		return t ;
	}

	private AlchemyItem buildAlchemyItem(RoleInstance role,
			Map<Byte, Integer> alchemyCountMap, Alchemy alchemy) {
		int roleLevel = role.getLevel();
		byte rewardType = alchemy.getRewardType();
		short buttonimageId = alchemy.getButtonImageId();
		// 奖励
		LevelRewardConfig rewardConfig = this.getLevelRewardConfig(roleLevel,
				rewardType);
		int rewardCount = (null == rewardConfig) ? 0 : rewardConfig
				.getRewardNumber();
		// 当天已经点金次数
		int hasAlchemyTimes = this.getHasAlchemyTimes(alchemyCountMap, rewardType);
		// 次数限制
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		AlchemyVipDailyConfig alchemyVipDailyConfig = this.getAlchemyVipDailyConfig(vipLevel, rewardType);
		short timesLimit = (null == alchemyVipDailyConfig) ? 0
				: alchemyVipDailyConfig.getTimesLimit();
		// 下次消耗的元宝数据
		AlchemyOutBreakConfig alchemyOutBreakConfig = this
				.getAlchemyOutBreakConfig(hasAlchemyTimes + 1);
		int expendMoney = (null == alchemyOutBreakConfig) ? 0
				: alchemyOutBreakConfig.getDiamandsConsume();
		AlchemyItem item = new AlchemyItem();
		item.setButtonimageId(buttonimageId);
		item.setExpendMoney(expendMoney);
		item.setRewardCount(rewardCount);
		item.setRewardType(rewardType);
		item.setTimesLimit(timesLimit);
		item.setTimesNow((short)Math.min(hasAlchemyTimes, timesLimit));
		return item;
	}

	
	
	private String getHeaderTipsStr(RoleInstance role) {
		String headerTipsStr = alchemyConfig.getHeaderTipsStr();
		if(Util.isEmpty(headerTipsStr)){
			return null ;
		}
		RoleCount rc = role.getRoleCount() ;
		// 未暴击的连续次数
		int hasNoOutBreakNum = Math.max(0,rc.getAlchemyNoBreakOutCount());
		int countNum = hasNoOutBreakNum + 1;
		AlchemyOutBreakConfig alchemyOutBreakConfig = this
				.getConfig4Crit(countNum);
		byte outBreakPercentageShow = (null == alchemyOutBreakConfig) ? 0
				: alchemyOutBreakConfig.getOutBreakPercentageShow();
		return MessageFormat.format(headerTipsStr,
				String.valueOf(outBreakPercentageShow),
				String.valueOf(alchemyConfig.getCritMultiple()));//c
	}

	private  Map<Byte, Integer> getRoleCountMap(RoleCount rc){
		String alchemyCountJsonStr = rc.getAlchemyCountJsonStr();
		Map<Byte, Integer> alchemyCountMap = Util
				.parseByteIntMap(alchemyCountJsonStr);
		if (alchemyCountMap == null) {
			alchemyCountMap = Maps.newHashMap();
		}
		return alchemyCountMap ;
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	
	private AlchemyResult doAlcemy(RoleInstance role,byte rewardType){
		AlchemyResult result = new AlchemyResult();
		AttributeType at = AttributeType.get(rewardType);
		if(null == at){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		Alchemy alchemy = this.getAlchemy(rewardType);
		if(null == alchemy){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		RoleCount rc = role.getRoleCount();
		//调用对是否当天进行判断
		rc.resetDay();
		Map<Byte, Integer> alchemyCountMap = this.getRoleCountMap(rc);
		//获得当天已经点金次数
		int hasTimes = this.getHasAlchemyTimes(alchemyCountMap, rewardType);
		//是否已经达到最大次数
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		AlchemyVipDailyConfig dailyConfig = this.getAlchemyVipDailyConfig(vipLevel, rewardType);
		int timesLimit = (null == dailyConfig)?0:dailyConfig.getTimesLimit();
//		if(hasTimes >= timesLimit){
		if(hasTimes >= timesLimit){
			result.setInfo(Status.Alchemy_Count_Limit.getTips());
			return result ;
		}
		//判断消耗
		AlchemyOutBreakConfig consumeConfig = getAlchemyOutBreakConfig(hasTimes + 1);
		if(null == consumeConfig){
			result.setInfo(this.getText(TextId.ERROR_DATA));
			return result ;
		}
		int expendMoney = consumeConfig.getDiamandsConsume();
		// 检验钻石是否足够
		if (role.getGoldMoney() < expendMoney) {
			result.setInfo(Status.Alchemy_Money_Not_Enough.getTips());
			return result ;
		}
		//获得奖励
		LevelRewardConfig rewardConfig = this.getLevelRewardConfig(role.getLevel(), rewardType);
		if(null == rewardConfig){
			result.setInfo(this.getText(TextId.ERROR_DATA));
			return result ;
		}
		//获得暴击
		int noCirtCount = Math.max(0, rc.getAlchemyNoBreakOutCount());
		AlchemyOutBreakConfig critConfig = this.getConfig4Crit(noCirtCount + 1);
		//计算是否暴击
		boolean isCrit = (RandomUtil.randomIntWithoutZero(FULL_CRIT_RATE) <= 
				critConfig.getOutBreakPercentage());
		//扣除元宝
		if (expendMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Decrease,
					expendMoney, OutputConsumeType.alchemy_consume);
		}
		int rewardNumber = rewardConfig.getRewardNumber();
		if(isCrit){
			rewardNumber *= this.alchemyConfig.getCritMultiple() ;
		}
		//添加用户获得
		GameContext.getUserAttributeApp().changeAttribute(role, at,
				OperatorType.Add, rewardNumber,
				OutputConsumeType.alchemy_award);
		//将当天次数+1
		alchemyCountMap.put(rewardType, hasTimes+1);
		//修改连续未暴击次数
		noCirtCount = isCrit ? 0 : (noCirtCount+1) ;
		//更新记录
		GameContext.getCountApp().setAlchemyCount(role, (byte)noCirtCount, 
				Util.byteIntMapToString(alchemyCountMap));
		//通知用户属性变化
		role.getBehavior().notifyAttribute();
		
		result.setCrit(isCrit);
		result.setRewardNum(rewardNumber);
		result.setAlchemy(alchemy);
		result.setAlchemyCountMap(alchemyCountMap);
		result.setAttriType(at);
		//标识成功
		result.success();
		return result ;
	}
	/**
	 * 或得暴击信息
	 * @param noCritTimes
	 * @date 2014-4-10 上午10:18:58
	 */
	private AlchemyOutBreakConfig getConfig4Crit(int noCritTimes){
		if(Util.isEmpty(this.alchemyOutBreakConfigMap) || noCritTimes <= 0){
			return null ;
		}
		AlchemyOutBreakConfig config = this.getAlchemyOutBreakConfig(noCritTimes);
		if(null != config){
			return config ;
		}
		//获得配置的最大次数(配置中包括0次)
		config = this.getAlchemyOutBreakConfig(this.alchemyOutBreakConfigMap.size());
		if(null == config){
			return null ;
		}
		if(noCritTimes >= config.getCountNumber()){
			return config ;
		}
		//此情况配置错误，直接返回NULL
		return null ;
	}

	
}
