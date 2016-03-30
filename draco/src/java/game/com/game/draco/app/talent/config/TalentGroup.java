package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentGroup implements KeySupport<Byte>{
	
	//类型
	private byte type;
	
	//大于等于
	private byte ge;
	
	//小于
	private int lt;
	
	//条件ID
	private byte conditionId;

	@Override
	public Byte getKey() {
		return getType();
	}
	
}
