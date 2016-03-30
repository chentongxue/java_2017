package com.game.draco.app.operate.firstpay.config;

import com.game.draco.app.operate.vo.OperateActiveBaseConfig;

import lombok.Data;

public @Data class FirstPayBaseConfig extends OperateActiveBaseConfig {

	private int minPoint;
	private int showHero;// 展示英雄资源Id
	
}
