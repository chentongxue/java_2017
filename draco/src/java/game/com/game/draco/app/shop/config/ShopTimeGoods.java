package com.game.draco.app.shop.config;


import com.game.draco.app.shop.domain.DateTimeBeanSupport;

import lombok.Data;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.Result;

public @Data class ShopTimeGoods extends DateTimeBeanSupport{
	
	private int goodsId;//物品ID
	private int goldPrice;//金条价格
	private int goldPriceDis;//金条折扣价格
	private int weight;//权重
	private int number;//商品数量
	private int decre;//递减量
	private int defaultBuyNum = 1 ; //默认购买数
	
	//
	private byte stateType;
	private byte bindType = BindingType.template.getType(); //购买物品绑定类型
	private BindingType bindingType;
	
	public Result init(){
		Result result = new Result();
		if(this.defaultBuyNum <=0){
			this.defaultBuyNum = 1 ;
		}
		String info = "goodsId=" + this.goodsId + ".";
		try {
			Result timeResult = this.initDateTimeBean();
			if(!timeResult.isSuccess()){
				return result.setInfo(info + timeResult.getInfo());
			}
			
			bindingType = BindingType.get(this.bindType);
			if(null == bindingType){
				return result.setInfo(info + "bindType config error!");
			}
			return result.success();
		} catch (Exception e){
			return result.setInfo(info + "catch exception: " + e.toString());
		}
	}
	
	
}
