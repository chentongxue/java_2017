package com.game.draco.app.skill.config;

public enum SkillEffectType {

	PHYSICS_HURT(0),//物理伤害
	MAGIC_HURT(1),//魔法伤害
	ASSISTANCE(2),//援助
	SKILL_HURT(3),//指技能的附加伤害
	;
	
	public int type;
	
	SkillEffectType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static SkillEffectType get(int type){
		for(SkillEffectType item : SkillEffectType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
