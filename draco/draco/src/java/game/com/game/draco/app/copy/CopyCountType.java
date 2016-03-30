package com.game.draco.app.copy;

public enum CopyCountType {
	
	Daily((byte)0,"Daily"),
	Weekly((byte)1,"Weekly"),
	
	;
	
	private final byte type;
	private final String name;

	CopyCountType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyCountType getCopyType(byte type){
		for(CopyCountType item : CopyCountType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
