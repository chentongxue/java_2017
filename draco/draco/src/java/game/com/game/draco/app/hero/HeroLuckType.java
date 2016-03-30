package com.game.draco.app.hero;

public enum HeroLuckType {
	low((byte)1),
	mid((byte)2),
	hight((byte)3),
	;
	
	private final byte type ;
	HeroLuckType(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}
	
	public static HeroLuckType get(int type){
		for(HeroLuckType t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
}
