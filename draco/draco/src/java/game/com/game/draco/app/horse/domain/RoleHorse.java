package com.game.draco.app.horse.domain;

import java.util.Map;

import com.game.draco.app.skill.domain.RoleSkillStat;
import com.google.common.collect.Maps;

import lombok.Data;

public @Data class RoleHorse{
	
	public final static String ROLE_ID = "roleId" ;
	
	public final static String HORSE_ID = "horseId" ;
	
	//角色Id
	private int roleId;
	//坐骑ID
	private int horseId;
	//品质
	private byte quality;
	//等级
	private short level;
	//经验
	private int exp;
	//状态 0未骑乘 1骑乘 
	private byte state;
	//骑术等级
	private short manshipLevel;
	//坐骑升级消耗数量
	private int levelUpHorseNum;
	//坐骑升阶消耗数量
	private int upgradeHorseNum;
	//是否删除
	private byte flag;
	//技能
	private Map<Short,RoleSkillStat> skillMap = Maps.newHashMap();
	
}
