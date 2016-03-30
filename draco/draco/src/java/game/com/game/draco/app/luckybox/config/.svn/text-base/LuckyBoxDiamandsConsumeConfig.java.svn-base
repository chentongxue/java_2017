package com.game.draco.app.luckybox.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * 付费玩的次数，钻石消耗
 * 
 */
public @Data class LuckyBoxDiamandsConsumeConfig  implements KeySupport<String>{
	private short feeTimes;
	private short dimandsConsume;


	@Override
	public String getKey() {
		return feeTimes +"";
	}
}
