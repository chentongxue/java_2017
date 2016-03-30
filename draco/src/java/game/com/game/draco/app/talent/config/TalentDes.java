package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentDes implements KeySupport<Integer>{
	
	//天赋ID
	private int talentId;
	
	//描述
	private String des;

	@Override
	public Integer getKey() {
		return getTalentId();
	}

}
