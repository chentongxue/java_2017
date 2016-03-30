package com.game.draco.app.union.domain;

import lombok.Data;

public @Data class UnionActivityCd implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

	//活动ID
	private byte activityId;
	
	//开始时间
	private long startTime;
	
	//结束时间
	private long endTime;
	
}
