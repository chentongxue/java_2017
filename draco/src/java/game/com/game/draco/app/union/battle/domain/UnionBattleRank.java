package com.game.draco.app.union.battle.domain;

import lombok.Data;

public @Data class UnionBattleRank {
//	private int battleId;		//公会战ID
	private int roleId;         //角色ID
	private int killNum;		//击杀数
	private int killedNum;		//被击杀数
	private int dkp;			//dkp
	
	private String unionId;
}
