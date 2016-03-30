package com.game.draco.app.camp.war.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class ConsequentKilledReward implements KeySupport<Integer>{

	private int killedNum ;
	private int campPrestige ;
	private int gameMoney ;
	
	@Override
	public Integer getKey(){
		return this.killedNum ;
	}
}
