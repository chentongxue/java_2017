package com.game.draco.app.equip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class StarWays implements KeySupport<String> {
	
	private short waysId ;
	private String waysName ;
	/*private byte waysType;
	private String parameter1 ;
	private String parameter2 ;*/
	private short forwardId ;
	
	@Override
	public String getKey() {
		return String.valueOf(waysId);
	}

}
