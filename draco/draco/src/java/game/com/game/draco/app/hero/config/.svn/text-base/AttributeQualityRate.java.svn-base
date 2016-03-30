package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class AttributeQualityRate extends HeroAttribute implements KeySupport<String> {
	
	private int quality ;
	private int star ;
	
	@Override
	public String getKey() {
		return this.quality + "_" + this.star ;
	}

}
