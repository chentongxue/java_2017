package com.game.draco.app.enhanceoption.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;
// xls:enhance_options sheet:options
public @Data class EnhanceOptionBase implements KeySupport<Short> {
	private short forwardId;
	private String forwardName;
	private short forwardResId;
	@Override
	public Short getKey() {
		return forwardId;
	}
}
