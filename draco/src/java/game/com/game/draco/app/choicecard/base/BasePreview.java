package com.game.draco.app.choicecard.base;

import lombok.Data;

/**
 * 抽卡描述数据
 * @author zhouhaobing
 *
 */
public @Data class BasePreview{

	//物品ID
	private int goodsId;
	
	//是否英雄
	private boolean hero;
	
}
