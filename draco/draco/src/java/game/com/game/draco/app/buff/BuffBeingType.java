package com.game.draco.app.buff;

public enum BuffBeingType {
	unique(0),
	coexist(1),
	;
	private final int type;

	public static BuffBeingType get(int type){
		for (BuffBeingType item : values()){
			if(item.getType() == type){
				return item ;
			}
		}
		return null ;
	}
	
	BuffBeingType(int type){
		this.type = type;
	}
	public int getType(){
		return type;
	}


}
