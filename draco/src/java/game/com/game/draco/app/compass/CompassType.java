package com.game.draco.app.compass;

public enum CompassType {
	
	taitan((byte)0,"泰坦遗迹"),
	julong((byte)1,"巨龙遗迹"),

	;
	
	private final byte type;
	private final String name;
	
	CompassType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static CompassType get(byte type){
		for(CompassType item : CompassType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
