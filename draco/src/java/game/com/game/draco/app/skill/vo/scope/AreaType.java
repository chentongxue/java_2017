package com.game.draco.app.skill.vo.scope;


public enum AreaType {
	target((byte)0),//地方
	self((byte)1),//自己
	;
	
	private final byte type;
	
	AreaType(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public static AreaType getType(byte type){
		for(AreaType st : values()){
			if(type == st.getType()){
				return st ;
			}
		}
		return null ;
	}
	
}
