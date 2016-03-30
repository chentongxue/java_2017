package com.game.draco.app.goblin.config;

import lombok.Data;

public @Data class GoblinSecretRewardConfig {

	private int dropGroupId;
	private byte goodsType;
	private int goodsId;
	private byte goodsNum;
	private byte goodsBind;
	private int probability;// 概率
	private int basePrice;// 底价
	
}
