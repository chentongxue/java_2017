package com.game.draco.app.goblin.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class GoblinGeneralRewardConfig implements KeySupport<String> {

	private String bossId;
	private int goldMoney;
	private int potential;
	
	@Override
	public String getKey() {
		return bossId;
	}
	
}
