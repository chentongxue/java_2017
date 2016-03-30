package com.game.draco.app.shop.config;

import lombok.Data;

public @Data class ShopSecretConfig {
	private short activeId;
	private byte moneyType;
	private int money;
	private int refreshCycle;
}
