package com.game.draco.app.choicecard.domain;

import lombok.Data;

public @Data class RoleChoiceCardLuck {
	
	public final static String ROLE_ID = "roleId";
	
	//角色ID
	private int roleId;
	
	//大类型 金币抽卡、钻石抽卡、活动抽卡
	private byte type;
	
	//幸运值
	private int luck;
	
	//首次免费抽
	private int freeNum;
	
	//金币首次免费
	private int goldFirstNum;
	
	
}
