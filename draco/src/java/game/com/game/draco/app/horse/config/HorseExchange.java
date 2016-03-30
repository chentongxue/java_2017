package com.game.draco.app.horse.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

/**
 * 坐骑基础数据
 * @author zhouhaobing
 *
 */
public @Data class HorseExchange implements KeySupport<Integer>{

	//坐骑ID
	private int horseId;

	//坐骑物品ID
	private int goodsId;
	
	//坐骑物品兑换数量
	private int exchangeNum;
	
	
	public Integer getKey(){
		return this.horseId ;
	}
}
