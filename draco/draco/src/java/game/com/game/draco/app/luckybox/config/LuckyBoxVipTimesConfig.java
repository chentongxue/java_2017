package com.game.draco.app.luckybox.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class LuckyBoxVipTimesConfig  implements KeySupport<String>{
	private byte vipLevel; //VIP等级
	private short vipTimes;
	@Override
	public String getKey() {
		return String.valueOf(vipLevel);
	}
}
