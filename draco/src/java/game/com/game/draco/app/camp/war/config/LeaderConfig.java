package com.game.draco.app.camp.war.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class LeaderConfig implements KeySupport<Byte> {

	private byte campId ;
	private String leaderName;
	private short leaderLevel	;
	private short leaderResId	;
	private byte leaderResRate ;

	@Override
	public Byte getKey(){
		return this.campId ;
	}
}
