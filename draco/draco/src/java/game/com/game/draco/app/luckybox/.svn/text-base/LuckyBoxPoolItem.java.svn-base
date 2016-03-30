package com.game.draco.app.luckybox;

import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.GameContext;

import lombok.Data;

/**
 * 每次随机出来的奖品
 * 是否是vip
 * 编号
 * 权重(新的权重)
 * 奖励物品/属性Id
 * 是否绑定
 */
public @Data class LuckyBoxPoolItem {
	private byte place;//1-8  1必为VIP,编号
	private byte vipFlag;// 1 为VIP
	private int awardId;
	private byte awardType;
	private int num;
	private int newOdds;
	private byte bind;
	private String awardKey;// del
	private byte coordinate;//已经开出的位置坐标 从1开始，若是未开启则为0
	
	/** 
	 * 获得奖励物品 
	 */
	public GoodsBase getAwardGoods(){
		return GameContext.getGoodsApp().getGoodsBase(this.awardId);
	}
}
