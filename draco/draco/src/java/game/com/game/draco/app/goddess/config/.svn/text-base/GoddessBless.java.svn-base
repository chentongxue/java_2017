package com.game.draco.app.goddess.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class GoddessBless implements KeySupport<Short>{
	private short curBless; //当前祝福值
	private short upgradeRate; //进化成功概率
	private short minBless; //进化失败奖励的祝福值下限
	private short maxBless; //进化失败奖励的祝福值上限
	
	@Override
	public Short getKey() {
		return this.curBless;
	}
}
