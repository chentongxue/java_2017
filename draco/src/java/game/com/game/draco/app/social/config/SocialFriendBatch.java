package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data
class SocialFriendBatch {

	private int count;// 每日加友次数
	private int roleNum;// 每次推送数量
	private int level;// 相差等级

	public void init(String info) {
		if (this.count <= 0) {
			this.checkFail(info + "count is error.");
		}
		if (this.roleNum <= 0) {
			this.checkFail(info + "roleNum is error.");
		}
		if (this.level <= 0) {
			this.checkFail(info + "level is error.");
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
