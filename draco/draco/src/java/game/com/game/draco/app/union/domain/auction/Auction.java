package com.game.draco.app.union.domain.auction;

import java.util.List;
import java.util.Set;

import lombok.Data;

public @Data class Auction extends UnionAuction{
	
	private static final long serialVersionUID = 1L;

	//活动ID
	private byte activityId;
	
	//组ID（击杀BOSS所属分组）
	private byte groupId;
	
	//击杀boss所有角色
	private Set<Integer> roleSet;
	
	//物品
	private List<GoodsItem> goodsList;
	
	//结束时间
	private long overTime;
	
	
}
