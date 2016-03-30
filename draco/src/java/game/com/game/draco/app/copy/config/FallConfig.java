package com.game.draco.app.copy.config;

import lombok.Data;

/**
 * 副本物品掉落(展示)
 */
public @Data class FallConfig {
	
	private short copyId;// 副本id
	private int fallGoodsId;// 副本掉落物品
	
}
