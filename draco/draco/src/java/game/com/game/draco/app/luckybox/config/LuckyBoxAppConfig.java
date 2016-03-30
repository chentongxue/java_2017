package com.game.draco.app.luckybox.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * 几轮，首次游戏币消耗
 * 
 */
public @Data class LuckyBoxAppConfig{
	private short roundsCount;
	private int firstDrawSilverMoneyConsume;//首次抽取宝箱消耗金币
	private String consumeSilverMoneyInfo;
	private String consumeDiamondsInfo;
;


}
