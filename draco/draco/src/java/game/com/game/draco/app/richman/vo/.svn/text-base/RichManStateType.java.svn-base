package com.game.draco.app.richman.vo;

public enum RichManStateType {
	Protect((byte)1, "protect"),
	Lull((byte)2, "lull"),
	;
	
	private final byte type;
	private final String name;
	
	RichManStateType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static RichManStateType get(int type) {
		for(RichManStateType secondType : RichManStateType.values()) {
			if(type != secondType.getType()) {
				continue;
			}
			return secondType;
		}
		return null;
	}
}
