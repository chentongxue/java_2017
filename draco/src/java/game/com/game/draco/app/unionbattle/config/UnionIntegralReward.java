package com.game.draco.app.unionbattle.config;

import lombok.Data;

public @Data class UnionIntegralReward{
	
	//奖励类型
	private byte rewType;
	
	//起始名次
	private int rankMin;

	//结束名次
	private int rankMax;
	
	//奖励组ID
	private int rewGroupId;

}
