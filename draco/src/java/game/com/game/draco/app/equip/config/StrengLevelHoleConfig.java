package com.game.draco.app.equip.config;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class StrengLevelHoleConfig implements KeySupport<Integer>{
	
	private int strengthenLevel;
	private byte hole;
	
	@Override
	public Integer getKey() {
		return this.strengthenLevel;
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.strengthenLevel + ":";
		if (this.strengthenLevel < 0) {
			this.checkFail(info + "strengthenLevel is config error!");
		}
		if (this.hole < 0) {
			this.checkFail(info + "hole is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
