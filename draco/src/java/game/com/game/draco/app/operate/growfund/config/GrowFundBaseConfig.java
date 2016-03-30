package com.game.draco.app.operate.growfund.config;

import lombok.Data;

import com.game.draco.app.operate.vo.OperateActiveBaseConfig;

public @Data class GrowFundBaseConfig extends OperateActiveBaseConfig{
	
	private int rechargePoint;// 充值多少钻石激活成长基金

}
