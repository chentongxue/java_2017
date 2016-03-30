package com.game.draco.app.skill.vo;

import lombok.Data;

/**
 * 技能某系伤害
 * A% + B
 */
public @Data class SkillHurt {
	private SkillHurtType type;
	private int percent; 
	private int value;
	
	public SkillHurt(SkillHurtType type, int percent, int value) {
		this.type = type;
		this.percent = percent;
		this.value = value;
	}
	
}
