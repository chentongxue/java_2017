package com.game.draco.app.union.battle.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class UnionBattleKilledMsgConfig implements KeySupport<Integer>{
	
	private int minNum;
	private int maxNum;
	private byte channelType;
	private String content;
	private int dkp;




	
	@Override
	public Integer getKey() {
		return minNum;
	}
}
