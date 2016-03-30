package com.game.draco.app.asyncarena.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

/**
 * 异步竞技场分组数据
 * @author zhouhaobing
 *
 */
public @Data class AsyncGroup implements KeySupport<Byte> {

	//组ID
	private byte groupId ;
	//大于
	private int gt;
	//小于
	private int lt ;
	
	@Override
	public Byte getKey(){
		return this.getGroupId();
	}
	
}
