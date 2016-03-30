package com.game.draco.app.survival.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class SurvivalReward implements KeySupport<Byte>{

	// 类型0参与 1胜利
	private byte type;

	// 金币
	private int gold;

	// honor
	private int honor;

	// 物品ID
	private int goodsId;
	
	// 物品数量
	private short goodsNum;
	
	// 物品是否绑定
	private byte goodsBinded;

	@Override
	public Byte getKey() {
		return getType();
	}

}
