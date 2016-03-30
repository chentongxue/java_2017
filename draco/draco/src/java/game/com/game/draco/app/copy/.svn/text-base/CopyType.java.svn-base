package com.game.draco.app.copy;

public enum CopyType {
	
	personal((byte)0,"个人副本"),
	team((byte)1,"组队副本"),
	union((byte)2,"公会副本")
//	faction((byte)2,"门派副本")
	
	;
	
	private final byte type;
	private final String name;

	CopyType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyType get(byte type){
		for(CopyType ct : CopyType.values()){
			if(ct.getType() == type){
				return ct;
			}
		}
		return null;
	}
}
