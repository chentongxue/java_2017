package com.game.draco.app.copy.line.config;

public enum CopyLineRewardStatus {
	
	Can_Not_Take((byte)0),//Can Not Take
	Can_Take((byte)1),//Can Take
	Received((byte)2)//Has Received
	
	;
	
	private final byte type;

	CopyLineRewardStatus(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	public static CopyLineRewardStatus getCopyType(byte type){
		for(CopyLineRewardStatus item : CopyLineRewardStatus.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
