package com.game.draco.app.shop.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.KeySupport;

public @Data class ShopSecretRuleConfig implements KeySupport<Integer> {
	private int ruleId;
	private int id;
	private int goodsId;
	private int goodsNum;
	private byte bindType;
	private int weight;
	private byte moneyType;
	private int money;
	//每次购买只购买一个，所以数量为1，启动时加载好
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	public void init(){
		if(goodsId <=0 || goodsNum <= 0) {
			return;
		}
		GoodsOperateBean bean = new GoodsOperateBean(goodsId, 1, bindType);
		goodsList.add(bean);
	}
}
