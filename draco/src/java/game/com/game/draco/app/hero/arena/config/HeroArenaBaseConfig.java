package com.game.draco.app.hero.arena.config;

import lombok.Data;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.chat.ChannelType;

@Data
public class HeroArenaBaseConfig {
	
	//商店ID
	private String shopId;
	
	// 世界广播
	private String broadcastInfo;
	
	public String getBroadCastTips(RoleInstance role) {
		if (Util.isEmpty(broadcastInfo)) {
			return "";
		}
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		return this.broadcastInfo.replace(Wildcard.Role_Name, roleName);
	}
	
}
