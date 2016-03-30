package com.game.draco.app.camp.war.config;

import lombok.Data;
import sacred.alliance.magic.vo.Point;

public @Data class RoleBattleConfig {

	private int matchCycle ;
	private int maxBattleTime ;
	private int clearBaffleTime ;
	private String roleBattleMapId ;
	private int role1X ;
	private int role1Y ;
	private int role2X ;
	private int role2Y ;
	private int leaderDays ;
	private int leaderHpRate ;
	private int leaderMinHp = 10000 ;
	private String desc ;
	
	public Point getPoint1(){
		return new Point(this.roleBattleMapId,role1X,role1Y);
	}
	
	public Point getPoint2(){
		return new Point(this.roleBattleMapId,role2X,role2Y);
	}
}
