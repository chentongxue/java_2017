package com.game.draco.app.unionbattle.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionIntegralSummon implements KeySupport<String>{
	
	//召唤NPCID
	private String npcId;
	
	//关系（1友方 0地方）
	private byte relation;

	//BUFFID
	private short buffId;
	
	//召唤方获得DKP
	private int summonDkp;
	
	//击杀方获得DKP
	private int killDkp;

	@Override
	public String getKey() {
		return getNpcId();
	}
	
}
