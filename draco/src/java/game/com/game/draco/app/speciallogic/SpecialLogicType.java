package com.game.draco.app.speciallogic;
public enum SpecialLogicType {
	frozen((byte)0),//NPC冰冻
    worldLevel((byte)1),//世界等级功能
	unionIntegral((byte)2),//公会名称
	;
	
	private final byte type;
	
	SpecialLogicType(byte type){
		this.type = type;
	}
	
	public final byte getType(){
		return type;
	}
	
	public static SpecialLogicType getType(byte type){
		for(SpecialLogicType st : values()){
			if(type == st.getType()){
				return st ;
			}
		}
		return null ;
	}
	
}
