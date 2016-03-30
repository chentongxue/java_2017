package com.game.draco.app.shopsecret.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;
import sacred.alliance.magic.constant.Cat;
/**
 * 每个商店从哪个【池子】取多少物品的【配置信息
 */
public @Data class ShopSecretPoolConfig implements KeySupport<String>{
	private String shopId;
	private String poolId;
	private int num;
	@Override
	public String getKey() {
		return shopId + Cat.underline + poolId;
	}
}
