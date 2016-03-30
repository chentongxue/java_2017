package com.game.draco.app.hero.arena;

public enum HeroFightStatus {
	
	Failure((byte) 0),
	Victory((byte) 1),
	;
	
	private final byte type;
	
	HeroFightStatus(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
}
