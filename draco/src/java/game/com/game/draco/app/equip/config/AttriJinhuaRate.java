package com.game.draco.app.equip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class AttriJinhuaRate extends AttributeSupport implements KeySupport<String>{

	private byte quality ;
	private byte star ;
	
	@Override
	public String getKey(){
		return genKey(this.quality ,this.star);
	}

	public static String genKey(int quality,int star){
		return quality + "_" + star ;
	}
}
