package com.game.draco.app.union.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class UnionActivityInfo implements KeySupport<Byte>{
	
	//功能ID
	private byte activityId;
	
	//功能名称
	private String activityName;
	
	//功能描述
	private String activityDes;
	
	//副本CD
	private int cd;
	
	//活动描述
	private String des;
	
	//活动类型
	private byte type;

	
	@Override
	public Byte getKey() {
		return getActivityId();
	}
}
