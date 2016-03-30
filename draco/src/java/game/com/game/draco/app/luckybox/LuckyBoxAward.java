package com.game.draco.app.luckybox;

import lombok.Data;

/**
 * 幸运宝箱奖励
 */
public @Data class LuckyBoxAward {
	private short id;           //
	private byte place;         //位置1-8
	
	private byte awardType;//1为物品，2为属性 金币和潜能等
	
	private int goodsId;        //奖品ID /属性ID
	private int goodsNum;        //奖品数量 /属性，随机得到
	private int bindType;        //绑定类型
	
	
	private String broadcastInfo;//奖励广播信息
}
