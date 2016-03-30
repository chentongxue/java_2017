package com.game.draco.app.skill.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class SkillBattleScoreC implements KeySupport<Integer>{
	
	//技能等级
	private int skillLevel;
	
	//全局
	private int c;

	@Override
	public Integer getKey() {
		return getSkillLevel();
	}
	
}
