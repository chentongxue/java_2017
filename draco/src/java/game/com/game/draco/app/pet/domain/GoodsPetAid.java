package com.game.draco.app.pet.domain;

import java.util.List;

import lombok.Data;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBasePetAidItem;

public @Data class GoodsPetAid extends GoodsBase {
	
	private int swallowExp;// 吞噬可获得经验

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBasePetAidItem item = new GoodsBasePetAidItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSwallowExp(swallowExp);
		item.setSecondType(secondType);
		return item;
	}

	@Override
	public void init(Object initData) {
	}

}
