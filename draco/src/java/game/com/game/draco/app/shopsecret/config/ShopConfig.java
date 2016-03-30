package com.game.draco.app.shopsecret.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
@Data
public class ShopConfig implements KeySupport<String>{
	private String shopId;
	private String shopName;
	private byte vipLevel;
//	private int minRoleLevel;
//	private int maxRoleLevel;
	
	private int generalRefreshTimes;

	/*
	 * true:可以刷新，false:不可刷新。加载的时候初始化
	 */
	private boolean canRefresh;
	@Override
	public String getKey() {
		return shopId;
	}
	
}
