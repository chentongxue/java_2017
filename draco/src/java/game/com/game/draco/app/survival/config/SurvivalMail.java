package com.game.draco.app.survival.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class SurvivalMail implements KeySupport<Byte>{

	// 邮件类型0参与 1胜利
	private byte type;

	// 邮件标题
	private String title;

	// 邮件内容
	private String content;

	@Override
	public Byte getKey() {
		return getType();
	}

}
