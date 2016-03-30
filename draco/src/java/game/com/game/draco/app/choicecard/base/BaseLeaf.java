package com.game.draco.app.choicecard.base;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * @author zhouhaobing
 *
 */
public @Data class BaseLeaf implements KeySupport<Integer>{

	//唯一标识ID 
	private int indexId;
	//父节点ID
	private int parentId ;
	//物品ID
	private int goodsId;
	//物品数量
	private int goodsNum;
	//概率
	private int prob;
	
	@Override
	public Integer getKey() {
		return getIndexId();
	}
	
}
