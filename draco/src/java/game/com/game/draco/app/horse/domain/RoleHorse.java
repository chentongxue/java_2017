package com.game.draco.app.horse.domain;

import java.util.List;

import lombok.Data;

import com.game.draco.base.QualityStarSupport;
import com.google.common.collect.Lists;

public @Data class RoleHorse implements QualityStarSupport{
	
	public final static String ROLE_ID = "roleId" ;
	
	public final static String HORSE_ID = "horseId" ;
	
	//角色Id
	private int roleId;
	//坐骑ID
	private int horseId;
	//品质
	private byte quality;
	//星级
	private byte star;
	//状态 0未骑乘 1骑乘 
	private byte state;
	//坐骑升星消耗数量
	private int starNum;
	//坐骑战力 排行榜使用
	private int battleScore;
	//技能
	private List<RoleHorseSkill> skillList = Lists.newArrayList();
	
}
