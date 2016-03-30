package com.game.draco.app.skill.config;

import lombok.Data;

public @Data class SkillBuff{
	
	//技能ID
	private short skillId;

	//技能相关
	private short buffId;
	
	//系数
	private int b;
	
	//附加值
	private int d;
	
	//描述
	private String des;
	
	//自己还是目标
	private byte targetType;
	
	//目标域
	private int areaId;
	
	//方案ID
	private int planId;
	
}
