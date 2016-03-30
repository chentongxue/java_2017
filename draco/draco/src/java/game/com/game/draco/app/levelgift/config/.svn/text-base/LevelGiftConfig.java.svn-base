package com.game.draco.app.levelgift.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

public @Data class LevelGiftConfig {
	private int level;
	
	private int goodsId1;
	private int goodsNum1;
	private int bindType1;
	
	private int goodsId2;
	private int goodsNum2;
	private int bindType2;
	
	private int goodsId3;
	private int goodsNum3;
	private int bindType3;
	
	private int goodsId4;
	private int goodsNum4;
	private int bindType4;
	
	private int goodsId5;
	private int goodsNum5;
	private int bindType5;
	
	private int goodsId6;
	private int goodsNum6;
	private int bindType6;
	
	private List<GoodsOperateBean> goodsRewards = new ArrayList<GoodsOperateBean>();
	
	public void init(){
		this.addGoodsToRewards(this.goodsId1, this.goodsNum1, this.bindType1);
		this.addGoodsToRewards(this.goodsId2, this.goodsNum2, this.bindType2);
		this.addGoodsToRewards(this.goodsId3, this.goodsNum3, this.bindType3);
		this.addGoodsToRewards(this.goodsId4, this.goodsNum4, this.bindType4);
		this.addGoodsToRewards(this.goodsId5, this.goodsNum5, this.bindType5);
		this.addGoodsToRewards(this.goodsId6, this.goodsNum6, this.bindType6);
	}
	
	private void addGoodsToRewards(int goodsId, int goodsNum, int bindingType){
		if(goodsId <= 0 || goodsNum <= 0){
			return ;
		}
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == goodsBase){
			Log4jManager.CHECK.error("goodsId="+goodsId+" is no exist in the levelgift");
			Log4jManager.checkFail();
			return;
		}
		this.goodsRewards.add(new GoodsOperateBean(goodsId, goodsNum, bindingType));
	}
	
}
