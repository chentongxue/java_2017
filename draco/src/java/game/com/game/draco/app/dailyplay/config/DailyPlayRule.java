package com.game.draco.app.dailyplay.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class DailyPlayRule implements KeySupport<String>{
	
	private int playId	 ;
	private byte playType	 ;
	private short roleLevel ;
	private String playName	 ;
	private String playDesc	 ;
	private short imageId	;
	private short requireNum ;
	private String parameter ;
	private short activeId ;
	private short forwardId ;

	public String getKey(){
		return String.valueOf(playId) ;
	}
}
