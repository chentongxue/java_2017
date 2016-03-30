package com.game.draco.app.recovery.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;

import com.game.draco.app.recovery.MultiKeySupport;
import com.game.draco.app.recovery.type.RecoveryConsumeType;
/**
 * 一键还原“消耗”的配置
 */
public @Data class RecoveryConsumeConfig extends RecoveryConsume implements  MultiKeySupport<String>{
	private String id;
	private byte vipLevel;
	
	@Override
	public String getKey() {
		return String.valueOf(id) + Cat.underline + String.valueOf(consumeType);
	}
	@Override
	public void init(){
		if(RecoveryConsumeType.getType(consumeType) == null){
			checkFail("onekey recovery OneKeyRecoveryConfig init  err," + "consumeType=" + consumeType + " is not exsit!");
		}
	}

	public boolean meetCondition(byte vipLevel) {
		return vipLevel >= this.vipLevel;
	}
	
	@Override
	public String getMultiKey() {
		return String.valueOf(id);
	}
}
