package com.game.draco.app.luckybox;

import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.GameContext;

import lombok.Data;

/**
 * 每次随机出来的奖品
 * 编号
 * 权重(新的权重)
 * 奖励物品/属性Id
 * 是否绑定
 */
public @Data class LuckyBoxPoolItem {
	private byte place;//1-8  1必为必出奖励,编号
	private int awardId;//goodsId
	private byte awardType;
	private int num;
//	private int newOdds;
	private byte bind;
	private String awardKey;//唯一
	private byte openFlag;//是否已经开出奖励，1为开启，若是未开启则为0
	
	/** 
	 * 获得奖励物品 
	 */
	public GoodsBase getAwardGoods(){
		return GameContext.getGoodsApp().getGoodsBase(this.awardId);
	}
}
