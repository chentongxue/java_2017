package com.game.draco.app.richman.vo;


public enum RichManCardTargetType {
	
	Front((byte)0, "front"),
	FrontBack((byte)1, "frontAndBack"),
	SelfTarget((byte)2, "selfOrTarget"),
	;
	
	private final byte type;
	private final String name;
	
	RichManCardTargetType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static RichManCardTargetType get(byte type) {
		for(RichManCardTargetType tt : RichManCardTargetType.values()) {
			if(type != tt.getType()) {
				continue;
			}
			return tt;
		}
		return null;
	}
}
