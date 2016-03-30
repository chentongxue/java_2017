package com.game.draco.app.pet.config;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

import lombok.Data;

public @Data class PetPvpConfig {
	private byte robNum; //抢夺次数
	private int robAwardId; //抢夺奖励宝箱id
	private short robAwardNum; //抢夺奖励宝箱数量
	private byte robAwardBind; //抢夺奖励宝箱绑定类型
	private byte revengeNum; //复仇次数
	private int revengeAwardId; //复仇奖励宝箱id
	private short revengeAwardNum; //复仇奖励宝箱数量
	private byte revengeAwardBind; //复仇奖励宝箱类型
	private int challengeNum;
	private String mapId;
	private int mapX;
	private int mapY;
	private int targetMapX;
	private int targetMapY;
	
	// 抢夺|复仇 礼包
	private GoodsOperateBean robAwardGoods;
	private GoodsOperateBean revengeAwardGoods;
	
	public void init() {
		robAwardGoods = this.initGoodsBean(robAwardId, robAwardNum, robAwardBind);
		revengeAwardGoods = this.initGoodsBean(revengeAwardId, revengeAwardNum, revengeAwardBind);
	}
	
	private GoodsOperateBean initGoodsBean(int goodsId, short num, byte bind) {
		if (goodsId <= 0 || num <= 0) {
			return null;
		}
		if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("PetConfig error:goodId=" + goodsId + " is not exsit!");
		}
		return new GoodsOperateBean(goodsId, num, bind);
	}
	
}
