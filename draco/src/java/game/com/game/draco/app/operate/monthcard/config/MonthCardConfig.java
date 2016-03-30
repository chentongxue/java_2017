package com.game.draco.app.operate.monthcard.config;

import com.game.draco.app.operate.vo.OperateActiveBaseConfig;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class MonthCardConfig extends OperateActiveBaseConfig {
	
	private int rechargeMoney;// 激活最少充值人民币
	private int rechargePoint;// 激活最少充值钻石
	private int rewardPoint;// 每天可领取钻石
	
	public Result init(String fileInfo) {
		Result result = new Result();
		if (this.rechargePoint <= 0) {
			result.setInfo(fileInfo + "rmbMoney is config error!");
			return result;
		}
		if (this.rewardPoint <= 0) {
			result.setInfo(fileInfo + "goldMoney is config error!");
			return result;
		}
		return result.success();
	}
	
}
