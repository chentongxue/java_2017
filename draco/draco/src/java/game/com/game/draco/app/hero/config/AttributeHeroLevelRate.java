package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class AttributeHeroLevelRate extends HeroAttribute implements KeySupport<String> {

	private int heroLevel;

	@Override
	public String getKey() {
		return String.valueOf(heroLevel);
	}

}
