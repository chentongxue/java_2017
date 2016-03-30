package com.game.draco.app.pet.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class PetPvpRefresh implements KeySupport<Integer>{
	private int level;
	private int silverMoney;
	
	@Override
	public Integer getKey() {
		return this.level;
	}
}
