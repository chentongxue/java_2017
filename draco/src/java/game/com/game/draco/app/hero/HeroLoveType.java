package com.game.draco.app.hero;

public enum HeroLoveType {
	horse((byte)0),
	pet((byte)1),
	godWeapon((byte)2),
	hero((byte)3),
	;
	
	private final byte type ;
	HeroLoveType(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}
	
	public static HeroLoveType get(int type){
		for(HeroLoveType t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
}
