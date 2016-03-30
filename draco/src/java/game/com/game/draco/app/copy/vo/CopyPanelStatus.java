package com.game.draco.app.copy.vo;

public enum CopyPanelStatus {
	
	Not_Open((byte)0),//未开启
	Finished((byte)1),//已完成
	Can_Enter((byte)2),//可进入
	
	;
	
	private final byte type;

	CopyPanelStatus(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
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
