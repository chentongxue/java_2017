package com.game.draco.app.horse.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * 坐骑骑术消耗
 * @author zhouhaobing
 *
 */
public @Data class ManshipConsume implements KeySupport<Integer>{

	//骑术等级
	private int manshipLevel;
	//消耗金币
	private int goldMoney;
	//消耗真气
	private int zp;
	//物品类型
	private byte goodsType;
	//物品ID
	private int goodsId;
	//物品数量
	private short goodsNum;
	
	@Override
	public Integer getKey(){
		return this.getManshipLevel();
	}
		
}
