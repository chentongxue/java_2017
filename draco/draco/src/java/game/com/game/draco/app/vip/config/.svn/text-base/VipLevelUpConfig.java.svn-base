package com.game.draco.app.vip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * each viplevel of rolevip has specific awards available
 * [vipExpMin,vipExpMax)
 */
public @Data class VipLevelUpConfig implements KeySupport<String>{
	private byte vipLevel;
	private int vipExpMin;
	private int vipExpMax;

	private int vipLevelUpAwardId;
	private short vipLevelUpAwardNum;
	private byte vipLevelUpAwardBind;
	private String  vipLevelUpAwardInfo;
	private String  vipLevelUpAwardInfo1;
	private int  vipLevelUpAwardImageId;
	
	private int vipDailyAwardId;
	private short vipDailyAwardNum;
	private byte vipDailyAwardBind;


	@Override
	public String getKey() {
		return vipLevel+"";
	}
	
	public boolean matchVipLevelConfig(int vipExp){
		return vipExpMin <= vipExp && vipExp < vipExpMax;
	}
}
