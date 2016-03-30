package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentConsume implements KeySupport<Byte>{
	
	//培养类型 0普通 1精心
	private byte type;
	
	//属性组
	private byte attrGroup;
	
	//物品组
	private byte goodsGroup;
	
	@Override
	public Byte getKey() {
		return getType();
	}
	
}
