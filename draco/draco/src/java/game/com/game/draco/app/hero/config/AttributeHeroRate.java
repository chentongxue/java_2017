package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class AttributeHeroRate extends HeroAttribute implements KeySupport<String> {

	private int heroId;

	@Override
	public String getKey() {
		return String.valueOf(heroId);
	}

}
