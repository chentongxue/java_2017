package com.game.draco.app.unionbattle.type;

public enum IntegralBattleStateType {
	
	nu((byte)2,(byte)0),
	success((byte)1,(byte)5),
	all((byte)0,(byte)3),
	failure((byte)-1,(byte)1),
	bye((byte)-2,(byte)3),
	;
	
	private final byte type;
	
	private final byte value;
	
	public byte getValue() {
		return value;
	}

	public byte getType() {
		return type;
	}

	IntegralBattleStateType(byte type,byte value){
		this.type = type;
		this.value = value;
	}
	
	public static IntegralBattleStateType get(byte type){
		for(IntegralBattleStateType t : values()){
			if(t.getType() == type){
				return t ;
			}
		}
		return null ;
	}
}
