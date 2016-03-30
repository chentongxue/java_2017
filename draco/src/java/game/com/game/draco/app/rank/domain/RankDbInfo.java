package com.game.draco.app.rank.domain;

import sacred.alliance.magic.constant.Cat;
import lombok.Data;
/**
 * 排行榜活动
 */
public @Data class RankDbInfo {
//数据库存值表示是否已领奖
	public static final byte REWARDED_NO = 0;
	public static final byte REWARDED_YES = 1;
	private int rankId; //活动排行榜id
	private String roleId; //角色id
	private byte reward;//是否领过奖 0：未领取，1：已领取
	private int count0; //数据1 对于1v1擂台赛来说记录的是：胜利场数
	private int count1; //数据2 对于1v1擂台赛来说记录的是：失败场数
	private int count2; //数据3
	
	private boolean existRecord;
	
	
	public String getSelfInfo(){
		StringBuffer sb = new StringBuffer();
		sb.append(rankId);
		sb.append(Cat.pound);
		sb.append(roleId);
		sb.append(Cat.pound);
		sb.append(reward);
		sb.append(Cat.pound);
		sb.append(count0);
		sb.append(Cat.pound);
		sb.append(count1);
		sb.append(Cat.pound);
		sb.append(count2);
		sb.append(Cat.pound);
		
		return sb.toString();
	}
}
