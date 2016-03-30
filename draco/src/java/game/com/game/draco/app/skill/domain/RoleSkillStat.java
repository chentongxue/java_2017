package com.game.draco.app.skill.domain;

import lombok.Data;

@Data
public class RoleSkillStat {
	
	public static final String ROLEID = "roleId";
	public static final String SKILLID = "skillId";
	
	private String roleId;
	private short skillId;
	private int skillLevel;
	private int addSkillLevel ; //附属的技能等级
	private long lastProcessTime;// 上次执行时间毫秒
	
}
