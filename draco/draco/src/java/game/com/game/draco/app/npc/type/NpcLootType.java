package com.game.draco.app.npc.type;

public enum NpcLootType {

	NORMAL(0),
	ALLINMAP(1),
	;
	
	int type;
	
	NpcLootType(int type){
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static NpcLootType getType(int type){
		switch(type){
		case 1:
			return ALLINMAP;	
		default:
			return NORMAL;
		}
	}
}
