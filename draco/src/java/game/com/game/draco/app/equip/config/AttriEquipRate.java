package com.game.draco.app.equip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class AttriEquipRate extends AttributeSupport implements KeySupport<String>{

	private int goodsId ;
	
	@Override
	public String getKey(){
		return String.valueOf(this.goodsId) ;
	}
	
}
