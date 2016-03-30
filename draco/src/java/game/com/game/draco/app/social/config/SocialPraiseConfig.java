package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialPraiseConfig {
	private int canHaveRecvTimes; // 最大能获得奖励的赞次数
	private int canGetGoodsTimes; // 能够得到奖励的被赞次数

	public void init(String info) {
		if (this.canHaveRecvTimes <= 0) {
			this.checkFail(info + "maxHaveRecvTimes is config error!");
		}
		if (this.canGetGoodsTimes <= 0) {
			this.checkFail(info + "canHaveRecvTimes is config error!");
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
}
