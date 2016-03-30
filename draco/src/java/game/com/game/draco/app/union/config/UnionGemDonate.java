package com.game.draco.app.union.config;

import lombok.Data;

public @Data class UnionGemDonate {
	
	//钻石
	private int gem;
	
	//最小次数
	private int min;
	
	//最大次数
	private int max;
	
	//公会获得人气
	private int contribute;
	
	//角色获得DKP
	private int addDkp;
	
	//每日最大捐献次数
	private byte maxCount;

}
