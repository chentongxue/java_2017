package com.game.draco.app.union.domain.auction;

import lombok.Data;

public @Data class RoleAuction extends UnionRoleAuction{
	
	private static final long serialVersionUID = 1L;

	//公会ID
	private String unionId;
	
	//角色ID
	private int roleId;
	
	//加价
	private int price;
	
}
