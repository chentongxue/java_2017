package com.game.draco.app.pet.config;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class PetListConfig {
	
	private int petId;// 宠物Id
	
	public void init(String fileInfo) {
		String info = fileInfo + this.petId + ":";
		if (this.petId <= 0) {
			this.checkFail(info + "petId is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
