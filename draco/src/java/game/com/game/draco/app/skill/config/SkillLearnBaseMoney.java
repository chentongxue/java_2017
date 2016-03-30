package com.game.draco.app.skill.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

@Data
public class SkillLearnBaseMoney implements KeySupport<Short>{
	
	private short skillId;
	
	private int b;
	
	private int d;

	@Override
	public Short getKey() {
		return getSkillId();
	}
	
}
