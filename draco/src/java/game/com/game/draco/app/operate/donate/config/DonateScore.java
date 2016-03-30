package com.game.draco.app.operate.donate.config;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.GameContext;

public @Data class DonateScore implements KeySupport<Integer>{
	private int goodsId; // 物品模板id
	private int score; // 积分
	
	public Result init(){
		Result result = new Result();
		if(goodsId <= 0 || null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
			result.setInfo("ActiveRankScore goodsId= " + this.goodsId + ", do not exsit!");
			return result.failure();
		}
		if(score <= 0){
			result.setInfo("ActiveRankScore goodsId= " + this.goodsId + ", score == 0");
			return result.failure();
		}
		return result.success();
	}

	@Override
	public Integer getKey() {
		return this.goodsId;
	}
}
