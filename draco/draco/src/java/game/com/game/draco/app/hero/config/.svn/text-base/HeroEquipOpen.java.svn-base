package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class HeroEquipOpen implements KeySupport<String>{

	private int equipPosId;
	private int roleLevel;
	private int goodsId;
	private short goodsNum;
	
	@Override
	public String getKey(){
		return String.valueOf(equipPosId);
	}

	public boolean isFree(){
		return !(this.goodsId > 0 && this.goodsNum > 0) ;
	}
}
