package com.game.draco.app.pet.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class AttributePetLevelConfig extends PetAttribute implements KeySupport<String> {

	private int level;

	@Override
	public String getKey() {
		return String.valueOf(level);
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.level + ":";
		if (this.level < 0) {
			this.checkFail(info + "petLevel is config error!");
		}
		super.init(); 
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
