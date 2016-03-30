package com.game.draco.app.asyncarena.vo;

import java.util.List;

import com.game.draco.message.item.GoodsLiteItem;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class RoleAsyncRankRewardResult extends Result {
	//时间
	private String time;
	//提示信息
	private String msg;
	//历史排行
	private int hisroryRanking;
	//奖励物品
	private List<GoodsLiteItem> goodsItem;
	
}
