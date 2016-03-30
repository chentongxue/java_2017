package com.game.draco.app.rank.domain;

import sacred.alliance.magic.vo.RoleInstance;
import lombok.Data;

/**
 * 排行榜app返回角色基本信息
 */
public @Data class RankLogRoleInfo {
	private short rank;
	private int roleId;
	private String roleName;
	private byte gender;
//	private byte career;
	private int level;
	private byte camp ;
	private String unionId ;
	
	public static RankLogRoleInfo getRoleInfo(RoleInstance role){
		RankLogRoleInfo roleInfo = new RankLogRoleInfo();
		roleInfo.setRoleId(role.getIntRoleId());
		roleInfo.setRoleName(role.getRoleName());
//		roleInfo.setCareer(role.getCareer());
		roleInfo.setGender(role.getSex());
		roleInfo.setLevel(role.getLevel());
		roleInfo.setCamp(role.getCampId());
		roleInfo.setUnionId(role.getUnionId());
		return roleInfo;
	}
}
