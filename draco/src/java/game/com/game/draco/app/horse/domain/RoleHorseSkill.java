package com.game.draco.app.horse.domain;

import lombok.Data;

public @Data class RoleHorseSkill{
	
	public final static String ROLE_ID = "roleId" ;
	
	public final static String HORSE_ID = "horseId" ;
	
	public final static String SKILL_ID = "skillId" ;
	
	//角色ID
	private int roleId;
	//坐骑ID
	private int horseId;
	//技能Id
	private short skillId;
	//等级
	private short level;
	//幸运值
	private int luck;

}
