package com.game.draco.app.union.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionDes implements KeySupport<Byte> {
	
	//类型
	private byte type;
	
	//描述
	private String describe;

	@Override
	public Byte getKey() {
		return getType();
	}

}
