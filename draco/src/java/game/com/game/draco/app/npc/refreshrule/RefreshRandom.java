package com.game.draco.app.npc.refreshrule;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class RefreshRandom implements KeySupport<String>{
	
	private int ruleId;//规则ID
	private int wave;//波次
	private int num;//随机个数
	
	@Override
	public String getKey(){
		return getRuleId() + Cat.underline + getWave();
	}
	
}
