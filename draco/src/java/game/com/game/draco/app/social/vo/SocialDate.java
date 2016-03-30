package com.game.draco.app.social.vo;

import lombok.Data;

public @Data class SocialDate {
	private long friendApplyTime = 0; // 好友请求响应时间
	private long transmissionApplyTime = 0; // 传功请求响应时间
}
