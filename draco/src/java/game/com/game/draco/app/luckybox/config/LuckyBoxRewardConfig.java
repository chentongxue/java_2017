package com.game.draco.app.luckybox.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * 奖励配置 lucky_box -> 
 */
public @Data class LuckyBoxRewardConfig implements KeySupport<String>{
//	private String vipLevel; //VIP等级0,1,1,2,normal
	private int id;	
	private int poolId;
	private int awardId;
	private byte awardType;
	private int numLower;
	private int numUpper;
	private byte bind;
	private int odds;

	@Override
	public String getKey() {
//		return vipLevel + Cat.underline + id;
		return String.valueOf(id);
	}
}
