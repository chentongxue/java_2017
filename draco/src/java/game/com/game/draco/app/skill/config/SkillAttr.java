package com.game.draco.app.skill.config;

import lombok.Data;

public @Data class SkillAttr{
	
	//技能ID
	private short skillId;
	
	//加或减
	private byte reduce;
	
	//属性类型
	private byte attrType;
	
	//百分比
	private int a;
	
	//属性值
	private int b;
	
	//附加值
	private int d;
	
	//自己还是目标
	private byte targetType;
	
	//修改目标属性
	private byte modifyTargetAttr;
	
	//描述
	private String des;
	
	//目标域
	private int areaId;
	
}
