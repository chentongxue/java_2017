package com.game.draco.app.union.domain.auction;

import lombok.Data;

public @Data class UnionRoleAuction implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public final static String UUID = "uuid" ;
	
	//公会ID
	private String unionId;

	//物品ID
	private String uuid;
	
	//角色ID
	private int roleId;
	
	//加价
	private int price;
	
	//出价时间
	private long createTime;
	
}
