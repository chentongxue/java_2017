package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class HeroLuck implements KeySupport<String>{

	private byte typeId;
	private int freeTimes;
	private int cd;
	private int goldMoney;
	private String tips	;
	private byte imageQuality ;
	
	@Override
	public String getKey(){
		return String.valueOf(this.typeId);
	}

}
