package com.game.draco.app.vip.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
/**
 * specific viplevel for client display
 */
public @Data class VipPrivilegeConfig implements KeySupport<String>{
	private byte vipLevel;
	
	private int vipPriType;
	private int vipPriPram;
	private String vipPriIntroduction;


	@Override
	public String getKey() {
		return vipLevel + Cat.underline + vipPriType;
	}
}
