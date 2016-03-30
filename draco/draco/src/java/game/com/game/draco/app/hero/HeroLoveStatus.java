package com.game.draco.app.hero;

public enum HeroLoveStatus {
	not_open((byte)-1),
	un_activated((byte)0),
	activated((byte)1),
	;
	
	private final byte type ;
	HeroLoveStatus(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}
	
	public static HeroLoveStatus get(int type){
		for(HeroLoveStatus t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
}
