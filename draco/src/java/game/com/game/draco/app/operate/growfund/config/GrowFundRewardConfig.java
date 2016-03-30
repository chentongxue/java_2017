package com.game.draco.app.operate.growfund.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class GrowFundRewardConfig implements KeySupport<Integer> {

	private int level;// 可领取等级
	private int value;// 可领取钻石
	
	@Override
	public Integer getKey() {
		return this.level;
	}
	
}
