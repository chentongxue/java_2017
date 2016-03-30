package com.game.draco.app.union.config.instance;

import lombok.Data;

public @Data class UnionActivityConsume{
	
	//起始次数
	private int beginNum;
	
	//结束次数 最大-1
	private int endNum;
	
	//消耗钻石
	private short gem;

	//消耗人气
	private int popularity; 
	
}
