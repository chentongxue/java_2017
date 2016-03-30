package com.game.draco.app.skill.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class SkillBattleScore implements KeySupport<Short>{
	
	//技能ID
	private short skillId;

	//技能相关
	private int b;
	
	//附加值
	private int d;

	@Override
	public Short getKey() {
		return getSkillId();
	} 

	
}
