package com.game.draco.app.skill.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class SkillHurt implements KeySupport<Short>{
	
	//技能ID
	private short skillId;
	
	//加或减
	private byte reduce;
	
	//伤害类型
	private byte hurtType;
	
	//万分比
	private int a;
	
	//技能相关
	private int b;
	
	//附加值
	private int d; 

	//自己还是目标
	private byte targetType;
	
	//属性类型
	private byte attrType;
	
	//修改目标类型
	private byte modifyTargetType;
	
	//描述
	private String des;
	
	//目标域
	private int areaId;
	
	//方案ID
	private int planId;
	
	@Override
	public Short getKey() {
		return getSkillId();
	}
	
}
