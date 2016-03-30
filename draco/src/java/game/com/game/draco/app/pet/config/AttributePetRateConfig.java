package com.game.draco.app.pet.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class AttributePetRateConfig extends PetAttribute implements KeySupport<String> {

	private int petId;
	
	public void init(String fileInfo) {
		String info = fileInfo + this.petId + ":";
		if (this.petId <= 0) {
			this.checkFail(info + "petId is config error!");
		}
		super.init();
	}

	@Override
	public String getKey() {
		return String.valueOf(petId);
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
