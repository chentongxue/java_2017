package com.game.draco.app.rank.type;

public enum RankActorType {

	ROLE((byte)0),
	UNION((byte)1),
	;

	private final byte type ;
	private RankActorType(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}

	
	
}
