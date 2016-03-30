package com.game.draco.app.unionbattle.type;

public enum IntegralBattleSuccessType {
	
	day((byte)0),
	week((byte)1),
	;
	
	private final byte type;

	public byte getType() {
		return type;
	}

	IntegralBattleSuccessType(byte type){
		this.type = type ;
	}
	
	public static IntegralBattleSuccessType get(int type){
		for(IntegralBattleSuccessType t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
}
