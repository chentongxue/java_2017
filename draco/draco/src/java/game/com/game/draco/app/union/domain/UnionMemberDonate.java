package com.game.draco.app.union.domain;

import lombok.Data;


/**
 * 公会捐献
 */
public @Data class UnionMemberDonate{
	
	//角色ID
	private int roleId;
	
	//捐献次数
	private int count;
	
}
