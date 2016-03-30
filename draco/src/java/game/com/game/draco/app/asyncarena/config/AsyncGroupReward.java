package com.game.draco.app.asyncarena.config;

import lombok.Data;

/**
 * 异步竞技场排行奖励数据
 * @author zhouhaobing
 *
 */
public @Data class AsyncGroupReward{

	//奖励组
	private int groupId;
	
	//奖励类型
	private byte attrType;
	
	//奖励数值
	private int attrValue;
	
}
