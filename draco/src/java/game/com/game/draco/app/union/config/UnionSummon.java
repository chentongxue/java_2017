package com.game.draco.app.union.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionSummon implements KeySupport<Integer>{
	
	//召唤组ID
	private int groupId;
	
	//召唤NPCID
	private String summonId;
	
	//召唤X
	private short summonX;
	
	//召唤Y
	private short summonY;
	
	//dkp
	private int dkp;
	
	//物品ID
	private int goodsId;
	
	//物品个数
	private short goodsNum;
	
	//传送X
	private short transferX;
	
	//传送Y
	private short transferY;

	@Override
	public Integer getKey() {
		return getGroupId();
	}
	
	

}
