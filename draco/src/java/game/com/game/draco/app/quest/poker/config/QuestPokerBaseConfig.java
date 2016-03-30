package com.game.draco.app.quest.poker.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class QuestPokerBaseConfig {
	
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	private int maxTimes;//每日最大轮数
    private int normalBuyTimes ;//普通用户可以购买的轮数
	private int refreshGold;//刷新消耗元宝
	private String describe;//说明

	
	public void init(String info){
		if(this.maxTimes <= 0){
			Log4jManager.CHECK.error(info + "maxTimes is error!");
			Log4jManager.checkFail();
		}
	}
	
}
