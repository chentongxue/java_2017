package com.game.draco.app.copy.line.config;

public enum CopyLineRewardStatus {
	
	Can_Not_Take((byte)0, "Can Not Take"),
	Can_Take((byte)1, "Can Take"),
	Received((byte)2, "Has Received")
	
	;
	
	private final byte type;
	private final String name;

	CopyLineRewardStatus(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
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
