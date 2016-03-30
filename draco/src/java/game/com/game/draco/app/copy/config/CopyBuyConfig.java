package com.game.draco.app.copy.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class CopyBuyConfig implements KeySupport<String>{
	
	private short copyId;//副本ID
	private short num;//购买第几次
	private int money;//花费金额
	
	@Override
	public String getKey() {
		return getCopyId() + Cat.underline + getNum();
	}
	
}
