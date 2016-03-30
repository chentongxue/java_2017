package com.game.draco.app.copy;

public enum CopyEnterType {
	
	Default((byte)0,"寻路到NPC"),
	SuoYaoTa((byte)1,"副本面板打开"),
	Copy_YaoShou((byte)3,"妖兽副本"),
	Copy_DiYu((byte)6,"地狱副本"),
	
	;
	
	private final byte type;
	private final String name;

	CopyEnterType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyEnterType getCopyType(byte type){
		for(CopyEnterType item : CopyEnterType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
