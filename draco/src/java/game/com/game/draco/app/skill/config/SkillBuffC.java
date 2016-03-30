package com.game.draco.app.skill.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class SkillBuffC implements KeySupport<String>{
	
	//技能等级
	private int skillLevel;
	
	//全局
	private int c;
	
	//方案ID
	private int planId;

	@Override
	public String getKey() {
		return getPlanId() + Cat.underline + getSkillLevel();
	}
	
}
