package com.game.draco.app.buff;

public enum BuffReplaceType {
	//需要二次确认
	confirm(-1),
	failure(0),
	replace(1),
	delay(2),
	reset(3),
	noReplace(4),
	;
	private final int type;

	public static BuffReplaceType get(int type){
		for (BuffReplaceType item : values()){
			if(item.getType() == type){
				return item ;
			}
		}
		return null ;
	}
	
	BuffReplaceType(int type){
		this.type = type;
	}
	public int getType(){
		return type;
	}


}
