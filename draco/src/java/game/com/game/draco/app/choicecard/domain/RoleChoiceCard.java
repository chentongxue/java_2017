package com.game.draco.app.choicecard.domain;

import lombok.Data;

public @Data class RoleChoiceCard {
	
	public final static String ROLE_ID = "roleId";
	
	//角色ID
	private int roleId;
	
	//大类型 金币抽卡、钻石抽卡、活动抽卡
	private byte type;
	
	//小类型 (花钱 十连抽)
	private byte specificType;
	
	private int freeNum;
	
	//花费次数
	private int num;
	
	//cd时间 金币免费抽 钻石免费抽
	private long cdTime;
	
}
