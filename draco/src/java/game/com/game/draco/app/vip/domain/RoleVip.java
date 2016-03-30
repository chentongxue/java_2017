package com.game.draco.app.vip.domain;

import lombok.Data;

public @Data class RoleVip {
	public static final String ROLE_ID = "roleId" ;
	
	private int roleId;
	private byte vipLevel;
	private int vipExp;
//	private Date lastReceiveAwardTime ; //delete
	private int vipLevelUpAward; // int 16HEX
	
	private int vipLevelFunction;//VIP功能是否开启
	
	private int vipLevelGift;   //【商城.运营】礼包
	
	public void updateVipLevelFunctionByLevel(int level){
		this.vipLevelFunction = vipLevelFunction|1<<(level-1);
	}
	
	public void updateVipLevelUpAwardByLevel(int level){
		this.vipLevelUpAward = vipLevelUpAward|1<<(level-1);
	}
	
	public void updateVipLevelGift(int level){
		this.vipLevelGift = vipLevelGift|1<<(level-1);
	}

}
