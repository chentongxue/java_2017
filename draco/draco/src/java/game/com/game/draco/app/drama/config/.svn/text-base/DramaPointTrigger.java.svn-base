package com.game.draco.app.drama.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class DramaPointTrigger implements KeySupport<Short>{
	private short dramaId;
	private String mapId;
	private short posX;
	private short posY;
	
	@Override
	public Short getKey() {
		return this.dramaId;
	}
	
}
