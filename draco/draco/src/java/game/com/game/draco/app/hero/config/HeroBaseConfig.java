package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsHeroAid;

public @Data
class HeroBaseConfig {
	private int valorGoodsId;
	private String valorDesc;
	private int justiceGoodsId;
	private String justiceDesc;
	
	private GoodsHeroAid valorGoodsBase ;
	private GoodsHeroAid justiceGoodsBase ;
	
}
