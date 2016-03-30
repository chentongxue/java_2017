package com.game.draco.app.union.battle.config;

import lombok.Data;

public @Data class UnionBattleAppConfig{
	
	private int dkp;
	private int dkpMaxLimit;
	private String desc;
	private String defenderExchangeInfo;			//攻守转换提示
	private byte defenderExchangeNotifySecondsLeft; //攻守转换倒计时
	private byte capitalMapIndex;					//主城ID（4）
	private int rolelevel;//最小等级限制
	private byte unionLevel;//最低公会等级限制
}
