package com.game.draco.app.skill.config;

public enum SkillApplyType {

	active((byte)0,"主动"),
	passive((byte)1,"被动");
	
	private final byte type;
	
	private final String name;
	
	SkillApplyType(byte type, String name){
		this.name=name;
		this.type=type;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static SkillApplyType get(byte type){
		for(SkillApplyType item : SkillApplyType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
