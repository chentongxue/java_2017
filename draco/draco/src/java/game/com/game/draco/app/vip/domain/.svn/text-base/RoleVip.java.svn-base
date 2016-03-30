package com.game.draco.app.vip.domain;

import java.util.Date;

import lombok.Data;

public @Data class RoleVip {
	public static final String ROLE_ID = "roleId" ;
	
	private int roleId;
	private byte vipLevel;
	private int vipExp;
	private Date lastReceiveAwardTime ; //
	private int vipLevelUpAward; // int 16HEX
	
	
	
	
	
	public void updateVipLevelUpAwardByLevel(int level){
		this.vipLevelUpAward = vipLevelUpAward|1<<(level-1);
	}

}
