package com.game.draco.app.union.domain.instance;

import lombok.Data;

public @Data class UnionActivityBossRecord implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//公会ID
	private String unionId;
	
	//活动ID
	private byte activityId;
	
	//groupId 
	private byte groupId;
	
	//Boss血量
	private long bossHp;
	
	//Boss状态
	private byte state;
	
	//最后操作时间
	private long lastTime;
	
}
