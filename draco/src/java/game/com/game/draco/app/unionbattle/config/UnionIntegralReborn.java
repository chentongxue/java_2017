package com.game.draco.app.unionbattle.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionIntegralReborn implements KeySupport<Byte>{
	
	//复活点ID
	private byte rebornId;
	
	//复活点名称
	private String rebornName;
	
	//mapX
	private int mapX;
	
	//mapY
	private int mapY;

	@Override
	public Byte getKey() {
		return getRebornId();
	}
	
}
