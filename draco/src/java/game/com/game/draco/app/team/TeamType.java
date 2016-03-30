package com.game.draco.app.team;

public enum TeamType {
	PLAY_TEAM(0,"play"),//普通小组队
	//COPY_TEAM(1,"copy"),// 副本小队
	//POINTRACE_TEAM(2,"pointrace"),//积分赛队伍
	//FACTION_TEAM(3,"faction"),//公会小组
	;
	
	public int type;
	public String name;
	TeamType(int type,String name){
		this.type = type;
		this.name = name;
	}
	public int getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static TeamType getTeamType(int type){
		for(TeamType teamType : TeamType.values()){
			if(teamType.getType() == type){
				return teamType;
			}
		}
		return null;
	}
}
