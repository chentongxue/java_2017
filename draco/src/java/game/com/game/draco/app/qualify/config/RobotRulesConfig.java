package com.game.draco.app.qualify.config;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class RobotRulesConfig {
	
	private short upRank;// 排名高的
	private short downRank;// 排名低的
	private byte greenNum;
	private byte blueNum;
	private byte purpleNum;
	private byte orangeNum;
	
	public void init(String fileInfo) {
		if (this.upRank <= 0) {
			this.checkFail(fileInfo + "upRank is config error!");
		}
		if (this.downRank <= 0) {
			this.checkFail(fileInfo + "downRank is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
