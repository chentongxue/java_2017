package com.game.draco.app.copy.line.config;

public enum CopyLineStatus {
	
	Not_Open((byte)0),//Did Not Open
	Can_Enter((byte)1),//Can Enter
	Passed((byte)2)//Has Passed
	
	;
	
	private final byte type;

	CopyLineStatus(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
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
