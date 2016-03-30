package com.game.draco.app.luckybox.config;

import com.game.draco.GameContext;

import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;
/**
 * 几轮，首次游戏币消耗
 * 
 */
public @Data class LuckyBoxAppConfig{
	private int defaultRoundsCount;
	private byte defaultOpenTimes = 1; //默认的免费开启次数
	private String consumeDiamondsInfo;
	
	private int firstCountPoolId;
	private int mustAwardPoolId;    //转盘8个物品中，玩家第一次转必出的奖品的pooId
	private int normalAwardPoolId;  //普通商品
	private int luckyTicketgoodsId; //可以增加刷新次数的
	private int refreshInterval;    //自动刷新时间间隔

	//
	private int fragmentGoodsId;//碎片GoodsId
	private int exchangeId; //兑换
	//兑换券的物品ID，兑换ID
	public void init(){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(luckyTicketgoodsId);
		if(gb == null){
			checkFail("LuckyBoxAppConfig init fail, luckyTicketgoodsId no exist");
		}
		gb = GameContext.getGoodsApp().getGoodsBase(luckyTicketgoodsId);
		if(gb == null){
			checkFail("LuckyBoxAppConfig init fail, fragmentGoodsId no exist");
		}
	}
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
}
