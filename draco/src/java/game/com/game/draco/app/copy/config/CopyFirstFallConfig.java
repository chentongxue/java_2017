package com.game.draco.app.copy.config;

import java.util.List;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import lombok.Data;

public @Data class CopyFirstFallConfig implements KeySupport<Short> {

	private short copyId;
	private int goodsId1;
	private int goodsNum1;
	private int bindType1;
	private int goodsId2;
	private int goodsNum2;
	private int bindType2;
	private int goodsId3;
	private int goodsNum3;
	private int bindType3;
	//--------------------------
	List<GoodsOperateBean> goodsList = Lists.newArrayList();

	@Override
	public Short getKey() {
		return this.copyId;
	}
	
	public void init(String fileInfo) {
		Result result = this.initGoodsBase(goodsId1, goodsNum1, bindType1);
		if (!result.isSuccess()) {
			this.checkFail(fileInfo + " goodsId1 is config error!");
		}
		result = this.initGoodsBase(goodsId2, goodsNum2, bindType2);
		if (!result.isSuccess()) {
			this.checkFail(fileInfo + " goodsId2 is config error!");
		}
		result = this.initGoodsBase(goodsId3, goodsNum3, bindType3);
		if (!result.isSuccess()) {
			this.checkFail(fileInfo + " goodsId3 is config error!");
		}
	}
	
	private Result initGoodsBase(int goodsId, int num, int bind) {
		Result result = new Result();
		if (goodsId <= 0 || num <= 0) {
			return result.success();
		}
		if (Util.isEmpty(this.goodsList)) {
			this.goodsList = Lists.newArrayList();
		}
		if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
			result.setInfo("FirstPayBaseConfig goodsId = " + goodsId + " is not exsit!");
			return result.failure();
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, num, bind));
		return result.success();
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
