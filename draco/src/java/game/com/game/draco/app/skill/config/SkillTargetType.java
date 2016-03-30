package com.game.draco.app.skill.config;

public enum SkillTargetType {

	//目标类型(服务器端)
	//[0任意|1敌方|2友方]
	all(0),
	enemy(1),
	friend(2),
	;
	
	public int type;
	
	SkillTargetType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static SkillTargetType get(int type){
		for(SkillTargetType item : values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
	
	
}
