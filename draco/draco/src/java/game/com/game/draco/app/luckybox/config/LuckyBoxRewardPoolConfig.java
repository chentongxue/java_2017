package com.game.draco.app.luckybox.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;
/**
 * VIP奖励池 等级_ID 0_1
 */
public @Data class LuckyBoxRewardPoolConfig implements KeySupport<String>{
	private String vipLevel; //VIP等级0,1,1,2,normal
	private int id;	
	private int awardId;
	private byte awardType;
	private int numLower;
	private int numUpper;
	private byte bind;
	private int odds;

	@Override
	public String getKey() {
		return vipLevel + Cat.underline + id;
	}
}
