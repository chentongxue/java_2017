package com.game.draco.app.shop.domain;

import com.game.draco.app.shop.config.ShopSecretRuleConfig;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsBase;

public @Data class ShopSecretResult extends Result{
	private ShopSecretRuleConfig shopSecretRuleConfig;
	private GoodsBase goodsBase;
	
	public ShopSecretResult setInfo(String info) {
		this.info = info;
		return this;
	}
}
