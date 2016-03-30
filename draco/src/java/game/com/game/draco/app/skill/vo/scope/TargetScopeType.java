package com.game.draco.app.skill.vo.scope;


public enum TargetScopeType {
	target((byte)0),//目标
	circle((byte)1),//圆
	tarea((byte)2),//T型
	cross((byte)3),//十字架
	;
	
	private final byte type;
	
	TargetScopeType(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public static TargetScopeType getType(byte type){
		for(TargetScopeType st : values()){
			if(type == st.getType()){
				return st ;
			}
		}
		return null ;
	}
	
}
