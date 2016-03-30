package com.game.draco.app.union.config;

import lombok.Data;

public @Data class UnionAuthority{

	//功能ID
	private byte funId;
	
	//功能名称
	private String funName;
	
	//会长 是否有权限
	private boolean leaderHold;
	
	//副会长 是否有权限
	private boolean deputyHold;
	
	//官员 是否有权限
	private boolean eliteHold;
	
	//会员 是否有权限
	private boolean memberHold;
	
}
