package com.game.draco.app.skill.vo;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;

import com.game.draco.app.skill.vo.SkillContext.AttrSource;

/**
 * 技能某系伤害
 * A% + B
 */
public @Data class SkillHurt {
	private SkillHurtType type;
	private int percent; 
	private int value;
	
	private AttrSource source = AttrSource.attacker ;
	private AttributeType attributeType = AttributeType.atk ;
	
	
	public SkillHurt(SkillHurtType type, int percent, int value,
			AttrSource source ,AttributeType attributeType) {
		this.type = type;
		this.percent = percent;
		this.value = value;
		this.source = source ;
		this.attributeType = attributeType ;
	}
	
	public SkillHurt(SkillHurtType type, int percent, int value) {
		this.type = type;
		this.percent = percent;
		this.value = value;
	}
}
