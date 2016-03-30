package com.game.draco.app.rune.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class MosaicRulesConfig implements KeySupport<Byte> {

	private byte hole;
	private byte type;

	@Override
	public Byte getKey() {
		return this.hole;
	}

	public void init(String fileInfo) {
		String info = fileInfo + this.hole + ":";
		if (this.hole <= 0) {
			this.checkFail(info + "hole is config error!");
		}
		if (this.type <= 0) {
			this.checkFail(info + "type is config error!");
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
