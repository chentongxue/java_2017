package com.game.draco.app.talent.fun;

public enum TalentType {
	Ordinary((byte)0),
	Elaborate((byte)1),
	;
	
	private final byte type ;
	TalentType(byte type){
		this.type = type ;
	}
	
	public byte getType() {
		return type;
	}
	
	public static TalentType get(int type){
		for(TalentType t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
}
