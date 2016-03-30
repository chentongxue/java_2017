package com.game.draco.app.goblin.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class GoblinLocationConfig implements KeySupport<String> {

	private String bossId;
	private int mapX;
	private int mapY;
	private int unionDkp;
	private int killBossDkp;
	private String dropgroupId;
	
	@Override
	public String getKey() {
		return this.bossId;
	}
	
}
