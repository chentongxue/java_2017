package com.game.draco.app.union.type;

public enum UnionActivityType {
	ACTIVITY_TYPE((byte)0),
	FORWORD_TYPE((byte)1),
	FUNCION_TYPE((byte)2),
	FUNCION_TYPE_MSG((byte)3),
	;
	
	private final byte type;
	
	UnionActivityType(byte type){
		this.type = type;
	}
	public final byte getType(){
		return type;
	}
	
	public static UnionActivityType get(byte type){
		for(UnionActivityType fr : UnionActivityType.values()){
			if(fr.getType() == type){
				return fr;
			}
		}
		return null;
	}
 }
