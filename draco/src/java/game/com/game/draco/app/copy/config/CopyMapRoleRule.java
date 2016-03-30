package com.game.draco.app.copy.config;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class CopyMapRoleRule {
	
	private String mapId;//地图ID
	private int minRoleLevel;//角色最低等级
	private int maxRoleLevel;//角色最高等级
	private String ruleId;//规则ID
	
	public boolean isSuitLevel(RoleInstance role){
		if(null == role){
			return false;
		}
		int level = role.getLevel();
		return level >= this.minRoleLevel && level <= this.maxRoleLevel;
	}
	
}
