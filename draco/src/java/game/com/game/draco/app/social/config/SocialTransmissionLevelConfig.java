package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialTransmissionLevelConfig implements KeySupport<Integer> {
	private int level;	//等级
	private int exp;	//经验
	private int expRecv;// 被传功者获得的经验
	
	public void init(String fileInfo) {
		String info = fileInfo + this.level + ".";
		if (this.level <= 0) {
			this.checkFail(info + "level is config error!");
		}
		if (this.exp <= 0) {
			this.checkFail(info + "exp is config error!");
		}
		if (this.expRecv <= 0) {
			this.checkFail(info + "expRecv is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public Integer getKey() {
		return this.level;
	}
}
