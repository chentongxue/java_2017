package com.game.draco.app.choicecard.activity.config;

import lombok.Data;

/**
 * @author zhouhaobing
 *
 */
public @Data class ActivityShow{

	//开始时间
	private String startTime;
	
	//结束时间
	private String endTime;
	
	//英雄ID1
	private int heroId1;

	//英雄ID2
	private int heroId2;

	//英雄ID3
	private int heroId3;

	//英雄ID4
	private int heroId4;

}
