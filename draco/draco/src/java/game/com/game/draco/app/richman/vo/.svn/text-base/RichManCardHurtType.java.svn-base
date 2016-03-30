package com.game.draco.app.richman.vo;


public enum RichManCardHurtType {
	
	Lose((byte)0, "lose"),
	Rob((byte)1, "rob"),
	;
	
	private final byte type;
	private final String name;
	
	RichManCardHurtType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static RichManCardHurtType get(byte type) {
		for(RichManCardHurtType ht : RichManCardHurtType.values()) {
			if(type != ht.getType()) {
				continue;
			}
			return ht;
		}
		return null;
	}
}
