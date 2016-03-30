package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentAttr implements KeySupport<Byte>{
	
	//组ID
	private byte groupId;
	
	//属性类型
	private byte attrType;
	
	//初始值
	private int attrValue;
	
	//最小值
	private int minLevel;
	
	//最大值
	private int maxLevel;
	
	@Override
	public Byte getKey() {
		return getGroupId();
	}
	
}
