package com.game.draco.app.skill.config;
/**
 * 技能变化（0:添加 1:删除 2:更新）
 * @author user
 *
 */
public enum SkillChangeType {
	Add((byte)0),
	Delete((byte)1),
	Update((byte)2),
	;
	
	private byte type;
	
	SkillChangeType(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
	public static SkillChangeType get(byte type){
		for(SkillChangeType changeType : SkillChangeType.values()){
			if(changeType.getType() == type){
				return changeType;
			}
		}
		return null;
	}
	
}
