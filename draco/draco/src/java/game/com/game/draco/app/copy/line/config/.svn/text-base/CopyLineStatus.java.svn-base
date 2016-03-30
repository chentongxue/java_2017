package com.game.draco.app.copy.line.config;

public enum CopyLineStatus {
	
	Not_Open((byte)0, "Did Not Open"),
	Can_Enter((byte)1, "Can Enter"),
	Passed((byte)2, "Has Passed")
	
	;
	
	private final byte type;
	private final String name;

	CopyLineStatus(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyLineStatus getCopyType(byte type){
		for(CopyLineStatus item : CopyLineStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
