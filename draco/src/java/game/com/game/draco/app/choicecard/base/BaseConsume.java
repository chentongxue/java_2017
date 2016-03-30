package com.game.draco.app.choicecard.base;

import lombok.Data;

/**
 * @author zhouhaobing
 *
 */
public @Data class BaseConsume{
	
	//类型
	private byte type;
	
	//次数
	protected int num;
	
	//最小等级
	private int minLevel;
	
	//最大等级
	private int maxLevel;
	
	//消费类型
	private byte consumptionType;
	
	//消费数量
	private int consumption;
}
