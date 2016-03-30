package com.game.draco.app.recovery.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.recovery.IRecoveryInitable;
import com.game.draco.app.recovery.MultiKeySupport;
import com.game.draco.app.recovery.type.RecoveryConsumeType;
/**
 * 一键还原 挂机经验的“消耗”的配置
 */
public @Data class RecoveryConsumeHungUpConfig implements KeySupport<String>, MultiKeySupport<String> ,IRecoveryInitable{

	protected byte consumeType;
	private int minLevel;
	private int maxLevel;
	protected int x;
	
	public int getConsumeValue(int exp, int expMax){
		double ratio = ((double)exp / (double)expMax);
		int cValue = (int)Math.ceil((x * ratio));
		//最小一钻石，或者一金币
		return cValue <=0 ? 1:cValue;
	}
	
	@Override
	public String getKey() {
		return String.valueOf(consumeType) 
		+ Cat.underline + String.valueOf(minLevel)
		+ Cat.underline + String.valueOf(maxLevel);
	}
	@Override
	public String getMultiKey(){
		return String.valueOf(consumeType); 
	}
	@Override
	public void init(){
		if(RecoveryConsumeType.getType(consumeType) == null){
			checkFail("onekey recovery RecoveryHungUpConsumeConfig init  err," + "consumeType=" + consumeType + " is not exsit!");
		}
	}
	
	public boolean meetCondition(int roleLevel) {
		if(roleLevel >= minLevel && roleLevel <= maxLevel){
			return true;
		}
		return false;
	}
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
}
