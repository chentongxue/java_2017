package com.game.draco.app.copy.vo;

public enum CopyShowType {
	
	Display((byte)0),//默认显示
	Have_Quest((byte)1),//拥有任务时显示
	Not_Display((byte)2),//不显示
	
	;
	
	private final byte type;

	CopyShowType(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	public static CopyShowType getCopyType(byte type){
		for(CopyShowType item : CopyShowType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
