package com.game.draco.app.goddess.config;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

import lombok.Data;

public @Data class GoddessConfig {
	private byte robNum; //抢夺次数
	private int robAwardId; //抢夺奖励宝箱id
	private short robAwardNum; //抢夺奖励宝箱数量
	private byte robAwardBind; //抢夺奖励宝箱绑定类型
	private short weakTime; //buff虚弱百分比
	private short weakRate; //buff虚弱百分比
	private byte revengeNum; //复仇
	private int revengeAwardId; //复仇奖励宝箱id
	private short revengeAwardNum; //复仇奖励宝箱id
	private byte revengeAwardBind; //复仇奖励宝箱id
	private short blessMax; //祝福最大值
	private float expRate ; //女神获得经验占角色获得经验百分比
	
	//变量
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
			Log4jManager.CHECK.error("GoddessConfig error:goodId=" + goodsId + " is not exsit!");
		}
		return new GoodsOperateBean(goodsId, num, bind);
	}
}
