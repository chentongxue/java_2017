package com.game.draco.app.copy;

public enum CopyPassJumpType {
	
	Enter_Point((byte)0,"进入副本前的坐标"),
	Tartet_Point((byte)1,"配置的目标点"),
	
	;
	
	private final byte type;
	private final String name;

	CopyPassJumpType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyPassJumpType getCopyType(byte type){
		for(CopyPassJumpType item : CopyPassJumpType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
