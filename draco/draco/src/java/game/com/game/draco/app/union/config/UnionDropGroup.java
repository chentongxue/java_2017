package com.game.draco.app.union.config;

import lombok.Data;

public @Data class UnionDropGroup {
	
	//groupId
	private int dropGroupId;
	
	//物品类型
	private byte goodsType;
	
	//物品ID
	private int goodsId;
	
	//物品数量
	private byte goodsNum;
	
	//是否绑定
	private byte goodsBind;
	
	//掉落概率
	private int probability;
	
	//底价
	private int basePrice;
	
}
