package com.game.draco.app.pet.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class AttributePetBornConfig extends PetAttribute implements KeySupport<String> {
	
	private int quality;
	private int star;
	
	@Override
	public String getKey() {
		return this.quality + "_" + this.star ;
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.quality + ":";
		if (this.quality < 0) {
			this.checkFail(info + "quality is config error!");
		}
		if (this.star < 0) {
			this.checkFail(info + "star is config error!");
		}
		super.init();
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
