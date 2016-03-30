package com.game.draco.app.unionbattle.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionIntegralNpc implements KeySupport<String>{
	
	//id
	private int id;
	
	//指挥官ID
	private String bossId;
	
	//mapX
	private int mapX;
	
	//mapY
	private int mapY;
	
	//奖励组ID
	private int rewGroupId;

	@Override
	public String getKey() {
		return getBossId();
	}
	
}
