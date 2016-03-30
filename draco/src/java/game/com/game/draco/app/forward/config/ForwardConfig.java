package com.game.draco.app.forward.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class ForwardConfig implements KeySupport<Short>{

	private short id ;
	private byte type ;
	private String parameter ;
	
	@Override
	public Short getKey(){
		return this.id ;
	}
}
