package com.game.draco.app.copy;

public enum CopyPanelStatus {
	
	Not_Open((byte)0,"未开启"),
	Finished((byte)1,"已完成"),
	Can_Enter((byte)2,"可进入"),
	
	;
	
	private final byte type;
	private final String name;

	CopyPanelStatus(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CopyPanelStatus getCopyType(byte type){
		for(CopyPanelStatus item : CopyPanelStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
