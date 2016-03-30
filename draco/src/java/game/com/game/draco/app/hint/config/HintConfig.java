package com.game.draco.app.hint.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class HintConfig implements KeySupport<Byte> {
	private byte id;
	private String uiTree;
	private byte parentId;
	
	@Override
	public Byte getKey() {
		return this.id;
	}
	
}
