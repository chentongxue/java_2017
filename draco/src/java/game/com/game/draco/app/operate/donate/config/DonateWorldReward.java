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
import com.game.draco.message.item.GoodsLiteNamedItem;

public @Data class DonateWorldReward implements KeySupport<Integer>{
	private int rewardId;// 奖励id
	private String name; //奖励名称
	private int goldMoney;
	private int bindMoney;// 绑金
	private int silverMoney;// 游戏币
	private int goods1;// 物品id
	private int num1;// 物品数量
	private int bind1;// 绑定类型
	private int goods2;// 物品id
	private int num2;// 物品数量
	private int bind2;// 绑定类型
	private int goods3;// 物品id
	private int num3;// 物品数量
	private int bind3;// 绑定类型
	private int goods4;// 物品id
	private int num4;// 物品数量
	private int bind4;// 绑定类型
	private int goods5;// 物品id
	private int num5;// 物品数量
	private int bind5;// 绑定类型
	
	private List<GoodsOperateBean> goodsList;
	
	public Result init(){
		Result result = initGoodsBase(goods1, num1, bind1);
		if(!result.isSuccess()){
			return result;
		}
		result = initGoodsBase(goods2, num2, bind2);
		if(!result.isSuccess()){
			return result;
		}
		result = initGoodsBase(goods3, num3, bind3);
		if(!result.isSuccess()){
			return result;
		}
		result = initGoodsBase(goods4, num4, bind4);
		if(!result.isSuccess()){
			return result;
		}
		result = initGoodsBase(goods5, num5, bind5);
		if(!result.isSuccess()){
			return result;
		}
		return result.success();
	}
	
	private Result initGoodsBase(int goodsId, int num, int bind){
		Result result = new Result();
		if (goodsId <= 0 || num <= 0) {
			return result.success();
		}
		if (this.goodsList == null) {
			this.goodsList = new ArrayList<GoodsOperateBean>();
		}
		if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
			result.setInfo("ActiveRankReward rewardId = " + this.rewardId + ",goodId=" + goodsId
					+ " is not exsit!");
			return result.failure();
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, num, bind));
		return result.success();
	}
	
	public List<GoodsLiteNamedItem> getGoodsLiteNamedList(){
		List<GoodsLiteNamedItem> awardList = new ArrayList<GoodsLiteNamedItem>();
		if(goldMoney > 0){
			awardList.add(GoodsHelper.getMoneyGoodsLiteNamedItem(AttributeType.goldMoney, (short)goldMoney));
		}
		if(silverMoney > 0) {
			awardList.add(GoodsHelper.getMoneyGoodsLiteNamedItem(AttributeType.gameMoney, (short)silverMoney));
		}
		awardList.addAll(GoodsHelper.getGoodsLiteNamedList(goodsList));
		return awardList;
	}

	@Override
	public Integer getKey() {
		return this.rewardId;
	}
}
