package com.game.draco.app.vip.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
/**
 * viplevel own special privileges
 */
public @Data class VipPrivilegeConfig implements KeySupport<String>{
	private byte vipLevel;//key	
	private int vipPriType;//key
	private int vipPriPram;
	private String vipPriIntroduction;
	private String param;//key

	private byte showFlag = 0;//是否展示给客户端，0：不展示;1：展示
	@Override
	public String getKey() {
		return vipLevel + Cat.underline + vipPriType + Cat.underline + param;
	}
	public boolean isShow(){
		return showFlag == 1?true:false;
	}
}
