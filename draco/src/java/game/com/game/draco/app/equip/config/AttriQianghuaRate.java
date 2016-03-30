package com.game.draco.app.equip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class AttriQianghuaRate extends AttributeSupport implements KeySupport<String>{

	private int level ;
	
	@Override
	public String getKey(){
		return String.valueOf(this.level); 
	}
}
