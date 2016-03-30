package com.game.draco.app.unionbattle.config;

import lombok.Data;

public @Data class UnionIntegralRewGroup{
	
	//奖励组ID
	private int groupId;
	
	//物品ID
	private int goodsId;
	
	//物品数量
	private short goodsNum;
	
	private int dkp;

}
