package com.game.draco.app.vip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
public @Data class VipLevelFunctionConfig implements KeySupport<String>{
	//vip fuc
	private String vipLevelFucId;
	private byte vipLevel;
	private int vipLevelFucOffButtionRes;
	private int vipLevelFucOnButtionRes;
	private byte vipLevelFucMoneyType;
	private int vipLevelFucMoney;
	private byte vipLevelFucType;
	private String vipLevelFucParam;

	@Override
	public String getKey() {
		return vipLevelFucId;
	}
	
}
