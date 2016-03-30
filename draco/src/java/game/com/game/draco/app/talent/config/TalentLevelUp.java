package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentLevelUp implements KeySupport<Integer>{
	
	//等级
	private int level;
	
	//总天赋点
	private int sumTalent;
	
	//每级最大天赋值
	private int maxTalent;

	@Override
	public Integer getKey() {
		return getLevel();
	}
	
}
