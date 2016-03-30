package com.game.draco.app.qualify.config;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class QualifyCostConfig implements KeySupport<Byte> {
	
	private byte times;// 购买第几次
	private int cost;// 消耗钻石
	private int cooldownCost;// 立即冷却消耗单位为钻石
	
	public void init(String fileInfo) {
		if (this.times <= 0) {
			this.checkFail(fileInfo + "times is config error!");
		}
		if (this.cost <= 0) {
			this.checkFail(fileInfo + "cost is config error!");
		}
		if (this.cooldownCost <= 0) {
			this.checkFail(fileInfo + "cooldownCost is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public Byte getKey() {
		return this.times;
	}
	
}
