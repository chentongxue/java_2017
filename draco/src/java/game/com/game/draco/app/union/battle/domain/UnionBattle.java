package com.game.draco.app.union.battle.domain;

import java.util.Date;

import lombok.Data;
/**
 * 公会战记录
 */
public @Data class UnionBattle {
	
	public final static String BATTLE_ID = "battleId" ;
	private int battleId;		//公会战ID
	private int roleId;        //杀死BOSS的角色ID
	
	private String unionId;		//目前防守的公会ID
	private Date killTime;		//杀死BOSS的时间
	private int winNumber;      //公会战公会连胜次数
	
	private String newMapName;
	private int instanceRenamedId;
	private int instanceId;
}
