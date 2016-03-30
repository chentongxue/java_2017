package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentRank implements KeySupport<Integer>{
	
	//天赋排名
	private int rank;
	
	//权重
	private int prob;
	
	@Override
	public Integer getKey() {
		return getRank();
	}
	
}
