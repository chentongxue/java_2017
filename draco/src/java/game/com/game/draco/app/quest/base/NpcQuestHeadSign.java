package com.game.draco.app.quest.base;

public enum NpcQuestHeadSign {
	None((byte)0),
	Accept((byte)1),
	Submit((byte)2),
    notComplete((byte)3),
	;
	
	public final byte type ;
	NpcQuestHeadSign(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}
	
	
}
