package com.game.draco.app.union.domain;

import lombok.Data;

public @Data class UnionActivity implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

	//公会ID
	private String unionId;
	
	//功能ID（副本ID）
	private byte activityId;
	
	//次数
	private byte num;
	
	//状态 0关闭 1开启
	private byte state;
	
}
