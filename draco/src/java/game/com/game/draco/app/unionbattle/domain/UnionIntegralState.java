package com.game.draco.app.unionbattle.domain;

import lombok.Data;

public @Data class UnionIntegralState {
	
	//轮数
	private int round;
	
	//排位
	private int grid;
	
	//对阵组
	private int groupId;
	
	//公会ID
	private String unionId;
	
	//积分(临时存储)
	private byte integral;
	
	//状态（2无状态 1胜利 0平 -1失败 -2轮空）
    private byte state;
	
}
