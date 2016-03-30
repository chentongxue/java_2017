package com.game.draco.app.asyncarena.domain;

import lombok.Data;

public @Data class AsyncBattleInfo {
	//被挑战者ID
	private int troleId;
	//状态 是否PK过
	private byte state;
	//是否胜利 
	private byte isSu;
	//挑战时间
	private long cTime;
	
	

}
