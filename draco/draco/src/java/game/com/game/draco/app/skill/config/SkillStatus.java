package com.game.draco.app.skill.config;

/**撮夔袨怓[0祥褫悝炾|1褫眕悝炾]*/
public enum SkillStatus {
	
	DoNotLearn((byte)0),
	CanLearn((byte)1),
	;
	
	private byte type;
	
	SkillStatus(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}
	
	public static SkillStatus get(byte type){
		for(SkillStatus status : SkillStatus.values()){
			if(status.getType() == type){
				return status;
			}
		}
		return null;
	}
}
