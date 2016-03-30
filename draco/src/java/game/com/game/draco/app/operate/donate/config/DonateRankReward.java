package com.game.draco.app.operate.donate.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;

public @Data class DonateRankReward implements KeySupport<String>{
	private String rankKey;
	private int goldMoney;
	private int bindMoney;
	private int silverMoney;
	private int goods1;
	private int num1;
	private byte bind1;
	private int goods2;
	private int num2;
	private byte bind2;
	private int goods3;
	private int num3;
	private byte bind3;
	private int goods4;
	private int num4;
	private byte bind4;
	private int goods5;
	private int num5;
	private byte bind5;
	//奖励物品列表
	private List<GoodsOperateBean> goodsList;
	
	private Result init(int goodsId, int num, byte bind) {
		Result result = new Result();
		if (goodsId <= 0 || num <= 0) {
			result.success();
			return result;
		}
		if (this.goodsList == null) {
			this.goodsList = new ArrayList<GoodsOperateBean>();
		}
		if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
			result.setInfo("rank reward," + "rankKey=" + this.rankKey
					 + ",goodId=" + goodsId	+ " is not exsit!");
			return result;
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, num, bind));
		result.success();
		return result;
	}
	
	public Result init(){
		Result result = this.init(goods1, num1, bind1);
		if(!result.isSuccess()) {
			return result;
		}
		
		result = this.init(goods2, num2, bind2);
		if(!result.isSuccess()) {
			return result;
		}
		
		result = this.init(goods3, num3, bind3);
		if(!result.isSuccess()) {
			return result;
		}
		
		result = this.init(goods4, num4, bind4);
		if(!result.isSuccess()) {
			return result;
		}
		
		result = this.init(goods5, num5, bind5);
		return result;
	}
	
	public List<GoodsLiteItem> getGoodsLiteList(){
		List<GoodsLiteItem> awardList = new ArrayList<GoodsLiteItem>();
		if(goldMoney > 0) {
			awardList.add(GoodsHelper.getMoneyGoodsLiteItem(AttributeType.goldMoney, (short)goldMoney));
		}
		if(silverMoney > 0) {
			awardList.add(GoodsHelper.getMoneyGoodsLiteItem(AttributeType.gameMoney, (short)silverMoney));
		}
		awardList.addAll(GoodsHelper.getGoodsLiteList(goodsList));
		return awardList;
	}
	

	@Override
	public String getKey() {
		return this.rankKey;
	}
}
