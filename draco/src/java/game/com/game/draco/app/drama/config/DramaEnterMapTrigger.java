package com.game.draco.app.drama.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class DramaEnterMapTrigger extends DramaTrigger implements KeySupport<String>{

	@Override
	public String getKey() {
		return this.mapId;
	}
}
