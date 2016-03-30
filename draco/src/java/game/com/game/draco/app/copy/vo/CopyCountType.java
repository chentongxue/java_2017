package com.game.draco.app.copy.vo;

public enum CopyCountType {
	
	Daily((byte)0),//Daily
	Weekly((byte)1),//Weekly
	
	;
	
	private final byte type;

	CopyCountType(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
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
