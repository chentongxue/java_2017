package com.game.draco.app.npc.type;

public enum NpcRankType {

	NORMAL(0),
	VIP(1),
	BOSS(2),
	;
	
	int type;
	
	NpcRankType(int type){
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static NpcRankType getType(int type){
		switch(type){
		case 0:
			return NORMAL;
		case 1:
			return VIP;
		default:
			return BOSS;	
		}
	}
	
	public static String toString(int type){
		switch(type){
		case 0:
			return "普通";
		case 1:
			return "精英";
		case 2:
			return "BOSS";
		default:
			return "其他";
		}
	}
	
	public static String[] getAllType(){
		String[] ret = new String[values().length];
		for(int i=0;i<values().length;i++){
			ret[i] = toString(values()[i].getType());
		}
		return ret;
	}
}
