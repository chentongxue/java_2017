package com.game.draco.app.recovery.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class RecoveryHangUpExpConfig implements KeySupport<Integer>{
	
	private int roleLevel;
	private int exp;
	
	@Override
	public Integer getKey() {
		return roleLevel;
	}
	public void init(){
		if(roleLevel <=0 || exp <= 0){
			checkFail("recovery.config.RecoveryHangUpExpConfig.init()fail: roleLevel or exp's value <=0 ");
		}
	}
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
}