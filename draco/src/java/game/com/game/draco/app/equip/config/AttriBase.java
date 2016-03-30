package com.game.draco.app.equip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class AttriBase extends AttributeSupport implements KeySupport<String>{

	private int goodsId	;
	private byte quality;
	private byte star ;
	
	@Override
	public String getKey(){
		return genKey(this.goodsId,this.quality,this.star) ;
	}
	
	protected boolean isRate(){
		return false ;
	}

	public static String genKey(int goodsId,int quality,int star){
		return goodsId + "_" + quality + "_" + star ;
	}
}
