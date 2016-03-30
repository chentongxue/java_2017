package com.game.draco.app.horse.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * 坐骑等级经验列表
 * @author zhouhaobing
 *
 */
public @Data class HorseExp implements KeySupport<String>{

	//精灵等级
	private short level ;
	//精灵经验
	private int exp ;
	//品质
	private byte quality;
	//物品ID
	private int goodsId;
	
	@Override
	public String getKey(){
		return this.getLevel() + Cat.underline + getQuality();
	}
	
}
