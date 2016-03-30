package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentGoods implements KeySupport<Byte>{
	
	//物品组ID
	private byte groupId;
	
	//物品ID
	private int goodsId;
	
	//物品类型
	private byte goodsType;
	
	//物品数量
	private short goodsNum;
	
	@Override
	public Byte getKey() {
		return getGroupId();
	}
	
}
