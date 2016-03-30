package com.game.draco.app.camp.war.config;

import lombok.Data;

import com.game.draco.message.item.GoodsLiteNamedItem;

public @Data class BattleRewardConfig {

	private int winCampPrestige ;
	private int winGameMoney ;
	private int failCampPrestige ;
	private int failGameMoney ;
	private int maxCampPrestige ;
	private int interruptPrestigeModulus ;
	private int interruptGameMoneyModulus ;
	private int lastWinGiftId ;
	private int lastFailGiftId ;
	
	private GoodsLiteNamedItem winGiftItem ;
	private GoodsLiteNamedItem failGiftItem ;
	
}
