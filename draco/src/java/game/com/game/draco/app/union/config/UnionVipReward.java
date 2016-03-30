package com.game.draco.app.union.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionVipReward implements KeySupport<Byte>{
	
	//公会等级限制
	private byte vipLevel;
	
	//物品类型
	private int rewardDkp;
	
	//加入公会时间
	private int joinTime;

	@Override
	public Byte getKey() {
		return getVipLevel();
	}
	
	
}
