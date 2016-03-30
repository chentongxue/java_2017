package com.game.draco.app.npc.type;
/**
 * 召唤类型
 */
public enum NpcSummonType {

	NORMAL((byte)0),
	TREASURE((byte)1),//虚空漩涡（藏宝图）召唤
	;
	
	byte type;
	
	NpcSummonType(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	public static NpcSummonType getType(byte type){
		switch(type){
		case 1:
			return TREASURE;	
		default:
			return NORMAL;
		}
	}
}
