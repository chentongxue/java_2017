package com.game.draco.app.copy.vo;

public enum CopySignType {
	
	Default((byte)0),//默认
	
	;
	
	private final byte type;

	CopySignType(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
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
