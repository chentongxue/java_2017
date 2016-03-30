package com.game.draco.app.union.type;

public enum UnionFunctionType {
	TERRITORY_TYPE((byte)0),//领地
	BUFF_TYPE((byte)1),//公会BUFF
	SUMMON_TYPE((byte)2),//召唤BOSS
	SHOP((byte)3),//公会商店
	;
	
	private final byte type;
	
	UnionFunctionType(byte type){
		this.type = type;
	}
	public final byte getType(){
		return type;
	}
	
	public static UnionFunctionType get(byte type){
		for(UnionFunctionType fr : UnionFunctionType.values()){
			if(fr.getType() == type){
				return fr;
			}
		}
		return null;
	}
 }
