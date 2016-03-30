package com.game.draco.app.skill.config;

public enum SkillSourceType {

	Role((byte) 0),
	Hero((byte) 1),
	Pet((byte) 2),
	Npc((byte) 3),
	Horse((byte) 4),
	;
	
	public byte type;
	
	SkillSourceType(byte type){
		this.type = type;
	}
	
	public byte getType(){
		return type;
	}
	
	public static SkillSourceType get(byte type){
		for(SkillSourceType item : SkillSourceType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
