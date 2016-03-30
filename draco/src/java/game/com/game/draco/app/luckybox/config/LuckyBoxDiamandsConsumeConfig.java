package com.game.draco.app.luckybox.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * 玩的次数，钻石消耗
 */
public @Data class LuckyBoxDiamandsConsumeConfig  implements KeySupport<String>{
	private short times;
	private short dimands;


	@Override
	public String getKey() {
		return times +"";
	}
}
