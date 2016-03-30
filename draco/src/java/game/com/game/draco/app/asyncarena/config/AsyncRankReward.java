package com.game.draco.app.asyncarena.config;

import lombok.Data;

/**
 * 异步竞技场排行奖励数据
 * @author zhouhaobing
 *
 */
public @Data class AsyncRankReward{

	//开始名次
	private short minRank;
	//结束名次
	private short maxRank;
//	//经验值
//	private int exp;
//	//真气
//	private int zp;
//	//金币
//	private int goldMoney;
//	//荣誉
//	private int honor;
	//物品ID 逗号分开
	private String goodsId;
	//物品数量
	private String goodsNum;
	//绑定类型
	private String bindType;
	
	//组ID
	private int groupId;

}
