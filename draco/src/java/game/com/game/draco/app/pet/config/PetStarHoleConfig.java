package com.game.draco.app.pet.config;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class PetStarHoleConfig implements KeySupport<String> {
	
	private byte star;// 当前星级
	private byte holeNum;// 当前星级孔数

	@Override
	public String getKey() {
		return String.valueOf(star);
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.star + ":";
		if (this.star < 0) {
			this.checkFail(info + "star is config error!");
		}
		if (this.holeNum < 0) {
			this.checkFail(info + "holeNum is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
