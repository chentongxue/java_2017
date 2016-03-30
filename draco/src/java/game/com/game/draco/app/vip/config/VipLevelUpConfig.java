package com.game.draco.app.vip.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.chat.ChannelType;
/**
 * each viplevel of rolevip has specific awards available
 * [vipExpMin,vipExpMax)
 */
public @Data class VipLevelUpConfig implements KeySupport<String>{
	private byte vipLevel;
	private int vipExpMin;
	private int vipExpMax;

	// 世界广播
	private String broadcastInfo;
	
	private int roleLevel;
	@Override
	public String getKey() {
		return vipLevel+"";
	}
	
	public boolean matchVipLevelConfig(int vipExp){
		return vipExpMin <= vipExp && vipExp < vipExpMax;
	}

	/**
	 * 获取世界喊话
	 * @param role
	 * @return
	 */
	public String getBroadCastTips(RoleInstance role) {
		if (Util.isEmpty(broadcastInfo)) {
			return "";
		}
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		String levelInfo = this.vipLevel + Util.getColor(ChannelType.Publicize_Personal.getColor());
		return this.broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.Number, levelInfo);
	}
	
}
