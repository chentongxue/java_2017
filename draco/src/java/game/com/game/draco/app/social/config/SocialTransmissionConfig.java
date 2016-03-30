package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialTransmissionConfig {
	private short maxTransmissionTimes;	//最大传功数
	private short maxReceiveTransmissionTimes;	//最大被传功数
	
	public void init(String info) {
		if (this.maxTransmissionTimes <= 0) {
			this.checkFail(info + "maxTransmissionTimes is config error!");
		}
		if (this.maxReceiveTransmissionTimes <= 0) {
			this.checkFail(info + "maxReceiveTransmissionTimes is config error!");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
}
