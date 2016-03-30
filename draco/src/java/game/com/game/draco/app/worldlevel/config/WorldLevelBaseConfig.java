package com.game.draco.app.worldlevel.config;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class WorldLevelBaseConfig {
	public static final float PROPORTION = 10000;
	
	private int minRank;
	private int maxRank;
	private int baseLevel;
	private int diffLevelMin;
	private int diffLevelMax;
	private int minRadio;
	private int maxRadio;
	private String desc;
	
	/**
	 * 初始化
	 * @param fileName
	 */
	public void init(String fileInfo) {
		if (this.minRank <= 0) {
			this.checkFail(fileInfo + " minRank=" + this.minRank + " is config error!");
		}
		if (this.maxRank <= this.minRank) {
			this.checkFail(fileInfo + " maxRank=" + this.maxRank + " is config error!");
		}
		if (this.baseLevel <= 0) {
			this.checkFail(fileInfo + " baseLevel=" + this.baseLevel + " is config error!");
		}
		if (this.diffLevelMin <= 0) {
			this.checkFail(fileInfo + " diffLevelMin=" + this.diffLevelMin + " is config error!");
		}
		if (this.diffLevelMax <= 0) {
			this.checkFail(fileInfo + " diffLevelMax=" + this.diffLevelMax + " is config error!");
		}
		if (this.minRadio < 0) {
			this.checkFail(fileInfo + " minRadio=" + this.minRadio + " is config error!");
		}
		if (this.maxRadio < this.minRadio) {
			this.checkFail(fileInfo + " maxRadio=" + this.maxRadio + " is config error!");
		}
	}
	
	/**
	 * 错误日志
	 * @param info
	 */
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
