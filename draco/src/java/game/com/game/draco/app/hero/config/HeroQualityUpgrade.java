package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class HeroQualityUpgrade implements KeySupport<String>{

	private byte quality ;
	private byte star ;
	private int nextShadowNum ;
	private byte astaff ;
	
	private HeroQualityUpgrade nextConf = null ;
	
	@Override
	public String getKey() {
		return this.quality + "_" + this.star ;
	}
}
