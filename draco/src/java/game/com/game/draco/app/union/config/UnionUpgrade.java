package com.game.draco.app.union.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionUpgrade implements KeySupport<Integer> {
	
	//公会等级
	private int level;
	
	//人气
	private int popularity;
	
	//活动ID(逗号分隔)
	private String activityId;
	
	//物品类型
	private byte goodsType;
	
	//物品ID
	private int goodsId;
	
	//物品数量
	private byte goodsNum;
	
	//最大人数
	private short maxMemberNum;
	
	//弹窗提示
	private String info;
	
	//buffId
	private short buffId;
	
	//buff等级
	private short buffLevel;
	
	//buff领取次数
	private int buffNum;
	
	//领取buff消耗DKP
	private int buffDkp;
	
	//召唤组ID
	private int groupId;
	
	@Override
	public Integer getKey(){
		return getLevel();
	}

}
