package com.game.draco.app.buff;

public enum BuffTimeType {
	single((byte)0),//单次
	continued((byte)1),//持续
	;
	private final byte type;
	
	BuffTimeType(byte type){
		this.type=type;
	}

	public byte getType() {
		return type;
	}

	public static BuffTimeType get(byte type){
		for(BuffTimeType tt : values()){
			if(tt.getType() == type){
				return tt ;
			}
		}
		return single ;
	}
}
