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
	private String cd = "";
	
	//活动描述
	private String des;
	
	//活动类型
	private byte type;
	
	//功能类型 0公会逻辑 1活动逻辑 2公会功能
	private byte funType;
	
	//参数 {0公会逻辑{不用填}，1活动逻辑{活动ID}，2公会功能{1传送到领地NPC, 2传送进领地}}
	private String param;
	
	//活动背景图
	private short imageId;
	
	//开启等级
	private String openLevel;
	
	@Override
	public Byte getKey() {
		return getActivityId();
	}
}
