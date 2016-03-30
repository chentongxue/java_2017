package com.game.draco.app.quest.poker.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
/**
 * 仅支持游戏币和钻石
 */
public @Data class PokerRoundBuyConsumeConfig implements KeySupport<Integer>{
	
	private	int buyTime;    // 第几次
	private int diamond;		//消耗

	
	public void init(){
		if(diamond <0 || buyTime < 0){
			Log4jManager.CHECK.error("pokerRoundBuyConsumeConfig init() err: diamond or buyTime err :[diamond, buyTime] = [" 
					+ diamond +", " + buyTime +"]");
			Log4jManager.checkFail();
		}
	}


	@Override
	public Integer getKey() {
		return buyTime;
	}
}
