package com.game.draco.app.alchemy.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;


/**
 * 等级对应的奖励
 * @author gaibaoning@moogame.cn
 * @date 2014-4-3 下午08:42:28
 */
public @Data class LevelRewardConfig implements KeySupport<String>{
	private byte roleLevel;
	private byte rewardType;// 奖励类型  0为点石成金得到金币，1为点化潜能获得升级
	private int rewardNumber;
	
	@Override
	public String getKey(){
		return roleLevel + Cat.underline + rewardType ;
	}
}
