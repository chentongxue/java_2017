package com.game.draco.app.talent.config;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.vo.AttributeOperateLevelBean;

import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Lists;

public @Data class TalentConsumeInfo implements KeySupport<Byte>{
	
	//培养类型 0普通 1精心
	private byte type;
	
	//属性组
	private List<AttributeOperateLevelBean> attrList = Lists.newArrayList();
	
	//物品组
	private List<GoodsLiteNamedItem> goodsGroup = Lists.newArrayList();
	
	@Override
	public Byte getKey() {
		return getType();
	}
	
}
