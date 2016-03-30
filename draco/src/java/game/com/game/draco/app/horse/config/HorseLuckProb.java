package com.game.draco.app.horse.config;

import lombok.Data;

/**
 * 坐骑基础数据
 * @author zhouhaobing
 *
 */
public @Data class HorseLuckProb {

	//幸运值方案ID
	private int schemeId;
	//幸运值下限
	private int luckLower;
	//幸运值上限
	private int luckHigh;
	//概率
	private  int prob;
	
}
