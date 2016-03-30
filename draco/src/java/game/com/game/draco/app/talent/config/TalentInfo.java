package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentInfo implements KeySupport<Integer>{
	
	//天赋ID
	private int talentId;
	
	//初始值
	private int attrValue;
	
	//天赋名称
	private String name;
	
	//天赋图标
	private short iconId;

	@Override
	public Integer getKey() {
		return getTalentId();
	}
	
}
