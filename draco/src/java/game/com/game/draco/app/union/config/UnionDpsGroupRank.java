package com.game.draco.app.union.config;

import lombok.Data;

public @Data class UnionDpsGroupRank{
	
	//组ID
	private byte groupId;
	
	//起始排名
	private int rankBefore;
	
	//结束排名
	private int rankEnd;
	
	//DKP奖励
	private int rewardDkp;

}
