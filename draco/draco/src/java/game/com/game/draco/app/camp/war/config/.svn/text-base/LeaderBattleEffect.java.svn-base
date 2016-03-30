package com.game.draco.app.camp.war.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class LeaderBattleEffect implements KeySupport<String> {

	private byte campId ;
	private byte battleType ;
	private short animationId ;
	private short attackerEffectId ;
	private short defenseEffectId ;
	
	@Override
	public String getKey(){
		return this.campId + "_" + this.battleType ;
	}
}
