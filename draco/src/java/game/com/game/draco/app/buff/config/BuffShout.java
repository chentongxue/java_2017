package com.game.draco.app.buff.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class BuffShout implements KeySupport<Short>{

	// BUFFID
	private short buffId;

	// BUFF喊话
	private String msg;
	
	@Override
	public Short getKey() {
		return getBuffId();
	}

}
