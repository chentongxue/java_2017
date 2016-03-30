package com.game.draco.app.shop.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;
/**
 * 提示信息
 */
public @Data class PanelInfoConfig implements KeySupport<Byte>{
	private byte vipLevel;
	private String info;
	@Override
	public Byte getKey() {
		return vipLevel;
	}
}
