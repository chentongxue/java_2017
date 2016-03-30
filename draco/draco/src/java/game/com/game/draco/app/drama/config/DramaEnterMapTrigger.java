package com.game.draco.app.drama.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class DramaEnterMapTrigger implements KeySupport<String>{
	private String mapId;
	private short dramaId;

	@Override
	public String getKey() {
		return this.mapId;
	}
}
