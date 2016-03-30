package com.game.draco.app.title;

public enum TitleStatus {
	Have((byte)0),
	Wear((byte)1),
	Lack((byte)2),
	;
	
	private final byte type ;

	private TitleStatus(byte type){
		this.type = type ;
	}

	public byte getType() {
		return type;
	}
	
	
}
