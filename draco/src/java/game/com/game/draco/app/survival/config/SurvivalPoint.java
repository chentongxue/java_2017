package com.game.draco.app.survival.config;

import lombok.Data;

public @Data class SurvivalPoint{

	// 地图ID
	private String mapId;

	// 战场人数量
	private int maximum;

	// 等待时间（分）
	private byte waitTime;

	// 获奖次数
	private byte rewardNum;
	
	// 活动ID
	private short activeId;
	
	// 玩法名称
	private String baseName;
	
	//玩法描述
	private String des;
	
}
