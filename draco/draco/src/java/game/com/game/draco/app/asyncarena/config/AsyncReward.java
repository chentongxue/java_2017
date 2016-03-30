package com.game.draco.app.asyncarena.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 异步竞技场奖励数据
 * @author zhouhaobing
 *
 */
public @Data class AsyncReward implements KeySupport<String> {

	//角色等级
	private int level;
	//游戏币
	private int goldMoney;
	//真气
	private int zp;
	//荣誉
	private int honor;
	//经验
	private int exp;
	//胜利失败
	private byte flag;
	
	@Override
	public String getKey(){
		return getLevel() + Cat.underline + getFlag();
	}
	
}
