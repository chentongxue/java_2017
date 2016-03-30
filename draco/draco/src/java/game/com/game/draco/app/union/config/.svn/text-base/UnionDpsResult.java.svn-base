package com.game.draco.app.union.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class UnionDpsResult implements KeySupport<String>{
	
	//组 支持多个BOSS
	private byte groupId;
	
	//BOSSId
	private String bossId;
	
	//击杀获得DKP奖励
	private int killBossDkp;
	
	//掉落组ID(逗号分割)
	private String dropgroupId;
	
	//基础血量百分比
	private int harmPercent;


	@Override
	public String getKey() {
		return getBossId();
	}
	
}
