package com.game.draco.app.luckybox.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class LuckyBoxOddsConfig  implements KeySupport<String>{
	private byte vipLevel; //类型为Byte
	private int times;	
	private int vipOdds;
	private int normalOdds;


	@Override
	public String getKey() {
		return vipLevel + Cat.underline+times;
	}
}
