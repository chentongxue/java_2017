package com.game.draco.app.goblin.config;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class GoblinRealConfig implements KeySupport<Byte> {

	private byte week;// 星期
	private byte number;// 真BOSS数量
	
	@Override
	public Byte getKey() {
		return this.week;
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + "week = " +  this.week + " : ";
		if (this.week <= 0 || this.week >= 8) {
			this.checkFail(info + "week is config error!");
		}
		if (this.number < 0 || this.number > 255) {
			this.checkFail(info + "number is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
