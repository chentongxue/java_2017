package com.game.draco.app.richman.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class RichManDicePrice implements KeySupport<Integer>{
	private int diceNum; //掷骰子次数
	private short goldMoney; //阶梯价格
	
	@Override
	public Integer getKey() {
		return this.diceNum;
	}
}
