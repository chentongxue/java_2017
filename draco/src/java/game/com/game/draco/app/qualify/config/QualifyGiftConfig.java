package com.game.draco.app.qualify.config;

import java.util.List;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

public @Data class QualifyGiftConfig {
	
	private short upRank;// 排名高的
	private short downRank;// 排名低的
	private int upLevel;// 等级上限
	private int downLevel;// 等级下限
	private int gameMoney;// 游戏币
	private int goldMoney;// 钻石
	private int potential;// 潜能
	private int honours;// 荣誉
	private int goodsId1;// 物品ID
	private int goodsNum1;// 物品数量
	private int bindType1 = -1;// 绑定类型
	private int goodsId2;
	private int goodsNum2;
	private int bindType2 = -1;
	private int goodsId3;
	private int goodsNum3;
	private int bindType3 = -1;
	
	private List<GoodsOperateBean> goodsList = Lists.newArrayList();
	
	public void init(String fileInfo) {
		if (this.upRank <= 0) {
			this.checkFail(fileInfo + "upRank is config error!");
		}
		if (this.downRank <= 0) {
			this.checkFail(fileInfo + "downRank is config error!");
		}
		if (this.upLevel <= 0) {
			this.checkFail(fileInfo + "upLevel is config error!");
		}
		if (this.downLevel <= 0) {
			this.checkFail(fileInfo + "downLevel is config error!");
		}
		if (this.goodsId1 > 0) {
			this.goodsList.add(this.createGoodsOperateBean(fileInfo, goodsId1, goodsNum1, bindType1));
		}
		if (this.goodsId2 > 0) {
			this.goodsList.add(this.createGoodsOperateBean(fileInfo, goodsId2, goodsNum2, bindType2));
		}
		if (this.goodsId3 > 0) {
			this.goodsList.add(this.createGoodsOperateBean(fileInfo, goodsId3, goodsNum3, bindType3));
		}
	}
	
	private GoodsOperateBean createGoodsOperateBean(String fileInfo, int goodsId, int goodsNum, int bindType) {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (null == goodsBase) {
			this.checkFail(fileInfo + "goodsId " + goodsId + " not exist!");
		}
		return new GoodsOperateBean(goodsId, goodsNum, bindType);
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
