package com.game.draco.app.copy;

public enum CopySignType {
	
	Default((byte)0,"默认"),
	
	;
	
	private final byte type;
	private final String name;

	CopySignType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopySignType getCopyType(byte type){
		for(CopySignType item : CopySignType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
