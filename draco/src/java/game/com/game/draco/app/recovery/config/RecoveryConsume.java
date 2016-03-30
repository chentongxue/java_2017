package com.game.draco.app.recovery.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.recovery.IRecoveryInitable;

public @Data abstract class RecoveryConsume  implements KeySupport<String>,IRecoveryInitable{
	
	protected byte consumeType;
	protected int value;
	protected int percentage;
	
	protected void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
	public double getPercent(){
		return (double)percentage/(double)10000;
	}
}