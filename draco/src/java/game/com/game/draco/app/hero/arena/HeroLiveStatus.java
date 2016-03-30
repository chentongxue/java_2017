package com.game.draco.app.hero.arena;

public enum HeroLiveStatus {
	
	Alive((byte) 0),
	Dead((byte) 1),
	;
	
	private final byte type;
	
	HeroLiveStatus(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
}
