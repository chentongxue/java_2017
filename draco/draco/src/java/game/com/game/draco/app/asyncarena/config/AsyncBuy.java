package com.game.draco.app.asyncarena.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 异步竞技场购买数据
 * @author zhouhaobing
 *
 */
public @Data class AsyncBuy implements KeySupport<String> {

	//VIP等级 -1为非VIP
	private byte vipLevel ;
	//购买次数
	private byte buyNum;
	//价格
	private int price;
	
	@Override
	public String getKey(){
		return getVipLevel() + Cat.underline + getBuyNum();
	}
	
}
