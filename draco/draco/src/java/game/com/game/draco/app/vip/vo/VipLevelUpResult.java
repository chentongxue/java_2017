package com.game.draco.app.vip.vo;

import com.game.draco.app.vip.domain.RoleVip;

import sacred.alliance.magic.base.Result;
import lombok.Data;

public @Data class VipLevelUpResult extends Result{
	private boolean isVipLevelUp;
	//result byte,info String
	private String roleId;
	private byte oldVipLevel;
	private byte newVipLevel; 
	
	private RoleVip roleVip;
}
