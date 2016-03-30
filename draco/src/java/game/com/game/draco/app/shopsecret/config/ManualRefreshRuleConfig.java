package com.game.draco.app.shopsecret.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

@Data
public class ManualRefreshRuleConfig  implements KeySupport<String>{
	private String shopId;
	private int times;
	private byte moneyType;
	private int money;
	@Override
	public String getKey() {
		return shopId + "_" + times;
	}
}
