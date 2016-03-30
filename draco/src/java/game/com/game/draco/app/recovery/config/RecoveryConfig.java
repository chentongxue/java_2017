package com.game.draco.app.recovery.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.recovery.IRecoveryInitable;
import com.game.draco.app.recovery.type.RecoveryType;
/**
 * 一键还原配置
 */
public @Data class RecoveryConfig implements KeySupport<String>,IRecoveryInitable{
	private String id;  //key
	private String name;
	private byte recoveryType;
	private int icon;
	private String param;//key

	@Override
	public String getKey() {
		return String.valueOf(id);
	}
	
	public void init(){
		//追回类型
		if(RecoveryType.getType(recoveryType) == null){
			checkFail("onekey recovery OneKeyRecoveryConfig init  err," + "recoveryType=" + recoveryType + " is not exsit!");
		}
	}

	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
	
	public String getParamKey(){
		return String.valueOf(recoveryType) + Cat.underline + param;
	}
}
