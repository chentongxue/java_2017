package com.game.draco.app.pet.config;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class PetLevelUpConfig implements KeySupport<String> {
	
	private byte quality;// 品质
	private int level;// 当前等级
	private int maxExp;// 最大经验（到下一级所需经验）

	@Override
	public String getKey() {
		return this.quality + "_" + String.valueOf(this.level);
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.quality + ":";
		if (this.quality < 0) {
			this.checkFail(info + "star is config error!");
		}
		if (this.level < 0) {
			this.checkFail(info + "level is config error!");
		}
		if (this.maxExp <= 0) {
			this.checkFail(info + "maxExp is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
