package com.game.draco.app.recovery.logic;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.recovery.RecoveryAppImpl;
import com.game.draco.app.recovery.config.RecoveryConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeHungUpConfig;
import com.game.draco.app.recovery.config.RecoveryOutPutConfig;
import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.type.RecoveryConsumeType;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.RecoveryConsumeItem;
import com.game.draco.message.item.RecoveryShowItem;
import com.google.common.collect.Lists;

/**
 * 挂机经验追回
 * 消耗金币或钻石 =（可追回经验占当天获得经验百分比）* X ，X在消耗表里，与角色等级有关
 * 产出为 0.8， 1， 2 
 * 需要记录：次数(固定值为1)
 */
public class RecoveryHangUpLogic extends RecoveryLogic{
	private static Logger logger = LoggerFactory.getLogger(RecoveryAppImpl.class);
	private static RecoveryHangUpLogic instance = new RecoveryHangUpLogic();
	private RecoveryHangUpLogic(){
	}
	
	public static RecoveryHangUpLogic getInstance(){
		return instance ;
	}

	@Override
	public List<RecoveryConsumeItem> getRecoveryConsumeItemList(
			final RoleRecovery recovery, byte vipLevel){
		return super.getRecoveryConsumeItemList(recovery, vipLevel);
	}

	@Override
	public RecoveryResult recoveryAwardAndConsume(RoleInstance role, String id, byte consumeType, int num){
		RecoveryResult result = new RecoveryResult();
		RoleRecovery rc = GameContext.getRecoveryApp().getRoleRecovery(role, id);
		if(rc == null){
			return result.failure();
		}
		if(!canRecovery(rc)){
			result.setInfo(getText(TextId.RECOVERY_COUNT_EMPTY));
			return result.failure();
		}
		//获得昨天可以获得的经验
		int exp = getOutPutExp(rc);
		//获得角昨天的角色等级
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		//获得消耗的配置
		RecoveryConsumeConfig  consumeCf = GameContext.getRecoveryApp().getRoleRecoveryConsumeConfig(id, consumeType ,vipLevel);
		if(consumeCf == null){
			result.setInfo(getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
			return result.failure();
		}
		RecoveryConsumeHungUpConfig consumeHungHupCf = GameContext.getRecoveryApp().getRecoveryConsumeHungUpConfig(consumeType, rc.getRoleLevel());
		if(consumeHungHupCf == null){
			result.setInfo(getText(TextId.RECOVERY_CONSUME_PARAM_HUNG_UP_ERROR));
			return result.failure();
		}
		int consume = getRecoveryAwardConsumeValue(rc, consumeCf, consumeHungHupCf);
		if(consumeType != RecoveryConsumeType.RECOVERY_CONSUME_FREE.getType()){//免费
			//获得消耗类型的属性
			AttributeType attr = RecoveryConsumeType.getType(consumeType).getAttributeType();
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attr, consume);
			if(ar.isIgnore()){//弹板
				return result.ignore();
			}
			if(!ar.isSuccess()){//不足
				return result.failure();
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					attr, OperatorType.Decrease,
					consume, OutputConsumeType.recover_consume);
		}

		double percent = consumeCf.getPercent();
		int expValue = (int)( exp * percent); //Math.round
		//添加经验
		role.getBehavior().changeAttribute(AttributeType.exp, OperatorType.Add, expValue);
		role.getBehavior().notifyAttribute();
		
		//保存追回次数
		rc.incrementNum();
		GameContext.getRecoveryApp().saveUpdateRecovery(rc);
		return result.success();
	}
	private int getOutPutExp(RoleRecovery rc){
		if(rc == null){
			return 0;
		}
		if(!canRecovery(rc)){
			return 0;
		}
		String ss[] = Util.splitString(rc.getData());
		if(ss == null || ss.length != 2){
			return 0;
		}
		try{
			return Integer.parseInt(ss[0]);
		}catch (Exception e) {
			return 0;
		}
	}
	@Override
	public RecoveryResult recoveryAward(RoleInstance role, String id, byte consumeType, int num){
		RecoveryResult result = new RecoveryResult();
		RoleRecovery rc = GameContext.getRecoveryApp().getRoleRecovery(role, id);
		if(rc == null){
			return result.failure();
		}
		if(!canRecovery(rc)){
			result.setInfo(getText(TextId.RECOVERY_COUNT_EMPTY));
			return result.failure();
		}
		try{
			//获得昨天可以获得的经验
			int exp = getOutPutExp(rc);
			//获得角昨天的角色等级
			byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			//获得消耗的配置,如果没有 说明请求非法
			RecoveryConsumeConfig  consumeConfig = GameContext.getRecoveryApp().getRoleRecoveryConsumeConfig(id, consumeType ,vipLevel);
			if(consumeConfig == null){
				result.setInfo(getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
				return result.failure();
			}
			double percent = consumeConfig.getPercent();
			int expValue = (int)( exp * percent); //Math.round
			//添加经验
			role.getBehavior().changeAttribute(AttributeType.exp, OperatorType.Add, expValue);
			role.getBehavior().notifyAttribute();
			//保存追回次数
			rc.incrementNum();
			GameContext.getRecoveryApp().saveUpdateRecovery(rc);
		}catch (Exception e) {
			logger.error("RecoveryHangUpLogic.recoveryAward err:" + e.toString());
		}
		
		return result.success();
	}
	
	@Override
	public int getRecoveryAwardConsumeValue(RoleInstance role, String id, byte consumeType) {
		RoleRecovery rc = GameContext.getRecoveryApp().getRoleRecovery(role, id);
		if(rc == null){
			return 0;
		}
		//消耗,先验证是否符合VIP等级，再验证是否符合角色等级
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		RecoveryConsumeConfig consumeCf = GameContext.getRecoveryApp().getRoleRecoveryConsumeConfig(rc.getRecoveryId(),consumeType, vipLevel);
		if(consumeCf == null){
			return 0;
		}
		RecoveryConsumeHungUpConfig consumeHungHupCf = GameContext.getRecoveryApp().getRecoveryConsumeHungUpConfig(consumeType, rc.getRoleLevel());
		if(consumeHungHupCf == null){
			return 0;
		}
		return getRecoveryAwardConsumeValue(rc, consumeCf, consumeHungHupCf);
	}
	/**
	 * 获得离线经验的消耗
	 * 消耗与角色的VIP（消耗是否有效），角色等级，未获得离线经验的比例有关
	 * @param rc 非空
	 * @param consumeCf 非空
	 * @param consumeHungHupCf 非空
	 */
	private int getRecoveryAwardConsumeValue(RoleRecovery rc,
			RecoveryConsumeConfig consumeCf, RecoveryConsumeHungUpConfig consumeHungHupCf) {
		String ss[] = Util.splitString(rc.getData());
		if(ss == null || ss.length != 2){
			return 0;
		}
		Integer exp = Integer.parseInt(ss[0]);
		Integer expMax = Integer.parseInt(ss[1]);
		if(exp == 0 || exp >expMax){
			return 0;
		}
		int consumeValue = consumeHungHupCf.getConsumeValue(exp, expMax);
		return consumeValue;
	}

	@Override
	protected RecoveryConsumeItem buildRecoveryConsumeItem(
			final RecoveryConsumeConfig consumeCf, 
			final RoleRecovery recovery) {
		//产出
		Collection<RecoveryOutPutConfig> outPutCfs = GameContext.getRecoveryApp().getRecoveryOutPutConfigs(recovery.getRecoveryId(), recovery.getRoleLevel()); 
		if(Util.isEmpty(outPutCfs)){
			return null;
		}
		return getRecoveryHangUpConsumeItem(consumeCf, recovery,outPutCfs);
	}
	/** 
	 * 
	 * @param consumeCf
	 * @param recovery
	 * @param outPutCfs
	 * @return
	 * @date 2014-10-25 下午03:16:06
	 */
	private RecoveryConsumeItem getRecoveryHangUpConsumeItem(
			final RecoveryConsumeConfig consumeCf, final RoleRecovery recovery,
			Collection<RecoveryOutPutConfig> outPutCfs) {
		RecoveryConsumeItem it = new RecoveryConsumeItem();
		
		String ss[] = Util.splitString(recovery.getData());
		if(ss == null || ss.length != 2){
			return null;
		}
		Integer exp = Integer.parseInt(ss[0]);
		Integer expMax = Integer.parseInt(ss[1]);
		if(exp == 0 || exp >expMax){
			return null;
		}
		//消耗
		RecoveryConsumeHungUpConfig consumeHungHupCf = GameContext.getRecoveryApp().getRecoveryConsumeHungUpConfig(consumeCf.getConsumeType(), recovery.getRoleLevel());
		
		it.setConsumeType(consumeCf.getConsumeType());
		
		int consumeValue = consumeHungHupCf.getConsumeValue(exp, expMax);
		it.setConsumeValue(consumeValue);// 消耗的金币，钻石等
		it.setOutPutPercent(consumeCf.getPercentage());
		return it;
	}
	public RecoveryShowItem getRecoveryShowItem(RecoveryConfig cf, RoleRecovery recovery,
			byte vipLevel){
		//产出
		Collection<RecoveryOutPutConfig> outPutCfs = GameContext.getRecoveryApp().getRecoveryOutPutConfigs(recovery.getRecoveryId(), recovery.getRoleLevel()); 
		if(Util.isEmpty(outPutCfs)){
			return null;
		}
		RecoveryShowItem it = new RecoveryShowItem();
		
		List<AttriTypeValueItem> attList = Lists.newArrayList();
		List<GoodsLiteNamedItem> goodsList = Lists.newArrayList();
		
		String ss[] = Util.splitString(recovery.getData());
		if(ss == null || ss.length != 2){
			return null;
		}
		Integer exp = Integer.parseInt(ss[0]);
		Integer expMax = Integer.parseInt(ss[1]);
		if(exp == 0 || exp >expMax){
			return null;
		}
		AttriTypeValueItem attItem = new AttriTypeValueItem();
		RecoveryOutPutConfig outputCf = (RecoveryOutPutConfig)outPutCfs.toArray()[0];
		
		//一键追回的产出从角色取得
		attItem.setAttriType((byte)outputCf.getOutputId());
		attItem.setAttriValue(exp);
		attList.add(attItem);
		
		it.setAttList(attList);
		it.setGoodsList(goodsList);
		it.setId(recovery.getRecoveryId());
		it.setIcon(cf.getIcon());
		it.setNum(recovery.getRecoveryNum());
		it.setRecoveryName(cf.getName());
		
		List<RecoveryConsumeItem>  cs = getRecoveryConsumeItemList(recovery, vipLevel);
		it.setConsumeList(cs);
		return it;
	}
	@Override
	public boolean canRecovery(RoleRecovery rc) {
		if(rc.getRecoveryNum() <= 0){//如果为0次的时候仍然展示，代码改为<
			return false;
		}
		String ss[] = Util.splitString(rc.getData());
		if(ss == null || ss.length != 2){
			return false;
		}
		Integer exp = Integer.parseInt(ss[0]);
		Integer expMax = Integer.parseInt(ss[1]);
		if(exp == 0 || exp >expMax){
			return false;
		}
		return true;
	}
}
