package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialTransmissionLevelmConfig implements KeySupport<Integer> {
	private int levelm;	//等级
	private int exp;	//经验
	
	public void init(String fileInfo) {
		String info = fileInfo + this.levelm + ".";
		if (this.levelm <= 0) {
			this.checkFail(info + "levelm is config error!");
		}
		if (this.exp <= 0) {
			this.checkFail(info + "exp is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public Integer getKey() {
		return this.levelm;
	}
}
