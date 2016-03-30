package com.game.draco.app.hero.arena.vo;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.Result;

import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedExItem;
import com.google.common.collect.Lists;

public @Data class HeroRewardResult extends Result {
	
	private List<AttriTypeValueItem> awardAttrList = Lists.newArrayList() ;//奖励属性
	private List<GoodsLiteNamedExItem> awardGoodsList = Lists.newArrayList() ;//奖励物品	
    
}
