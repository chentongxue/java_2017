package com.game.draco.app.skill.domain;

import lombok.Data;

public @Data
class SBuffC {

	// 技能相关
	private short buffId;

	// 系数
	private int b;

	// 附加值
	private int d;

	private int c;
	
	//自己还是目标
	private byte targetType;
	
	//目标域
	private int areaId;
	
}
