package com.game.draco.app.rune.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data
class RuneCostConfig implements KeySupport<String> {

	private int level;
	private int smeltMoney;

	@Override
	public String getKey() {
		return ""+level;
	}

	public void init(String fileInfo) {
		String info = fileInfo + this.level + ":";
		if (this.level < 0) {
			this.checkFail(info + "level is config error!");
		}
		if (this.smeltMoney < 0) {
			this.checkFail(info + "smeltMoney is config error!");
		}

	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
