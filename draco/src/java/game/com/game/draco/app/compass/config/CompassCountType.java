package com.game.draco.app.compass.config;

public enum CompassCountType {
	
	OneTimes((byte)1),
	TenTimes((byte)10),
	FiftyTimes((byte)50),
	
	;
	
	private final byte type;//抽奖次数

	CompassCountType(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	

	public static CompassCountType get(int type){
		for(CompassCountType item : CompassCountType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
