package com.game.draco.app.shop.domain;

import lombok.Data;

public @Data class ShopTimeParam {
	
	private int goodsNum;//物品总数目
	private int refreshTime;//刷新周期
	private String content;//刷新广播
	
}
