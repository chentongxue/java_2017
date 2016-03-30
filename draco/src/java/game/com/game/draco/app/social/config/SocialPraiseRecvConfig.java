package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialPraiseRecvConfig implements KeySupport<Integer> {
	private int level;	//等级
	private int exp;	//经验
	private int gold;	//金币
	private int prof;	//潜能
	
	@Override
	public Integer getKey(){
		return this.level ;
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.level + ".";
		if (this.level <= 0) {
			this.checkFail(info + "level is config error!");
		}
		if (this.exp < 0) {
			this.checkFail(info + "exp is config error!");
		}
		if (this.gold < 0) {
			this.checkFail(info + "gold is config error!");
		}
		if (this.prof < 0) {
			this.checkFail(info + "prof is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
}
