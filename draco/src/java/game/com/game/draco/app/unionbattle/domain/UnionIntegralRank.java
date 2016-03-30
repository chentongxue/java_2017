package com.game.draco.app.unionbattle.domain;

import lombok.Data;

public @Data class UnionIntegralRank {
	
	//公会ID
	private String unionId;
	
	//积分
	private int integral;
	
	//历史积分
	private int oldIntegral;
	
	//重置时间
	private long resetTime;
	
}
