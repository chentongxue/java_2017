package com.game.draco.app.operate.donate.domain;

import sacred.alliance.magic.constant.Cat;
import lombok.Data;
/**
 * 乐翻天活动
 */
public @Data class RoleDonate {
	//数据库存值表示是否已领奖
	public static final byte REWARDED_NO = 0;
	public static final byte REWARDED_YES = 1;
	private int rankId; //活动排行榜id
	private String roleId; //角色id
	private int curCount; //活动计数
	private byte reward;//是否领过奖 0：未领取，1：已领取
	private int worldReward; //全民领奖标识0-4位分别标识1-5档奖励
	
	private boolean existRecord;
	private boolean changed = false ;
	
	
	public String getSelfInfo(){
		StringBuffer sb = new StringBuffer();
		sb.append(rankId);
		sb.append(Cat.pound);
		sb.append(roleId);
		sb.append(Cat.pound);
		sb.append(curCount);
		sb.append(Cat.pound);
		sb.append(reward);
		sb.append(Cat.pound);
		sb.append(worldReward);
		sb.append(Cat.pound);
		
		return sb.toString();
	}
}
