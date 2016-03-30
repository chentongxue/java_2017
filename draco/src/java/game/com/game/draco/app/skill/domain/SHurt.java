package com.game.draco.app.skill.domain;

import lombok.Data;

public @Data
class SHurt {
	
	// 加或减
	private byte reduce;

	// 伤害类型
	private byte hurtType;

	// 万分比
	private int a;

	// 技能相关
	private int b;

	// 附加值
	private int d;

	// 自己还是目标
	private byte targetType;
	
	// 属性类型
	private byte attrType;

	//修改目标类型 0自己 1目标
	private byte modifyTargetType;
	
	// 百分比中的属性类型
	private byte at;
	
	// 百分比
	private int c;
	
	//目标域
	private int areaId;
	
}
