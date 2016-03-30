package com.game.draco.app.npc.type;

public enum NpcType {

	npc(1),//功能npc & monster
	monster(2),//怪物
	building(19), //建筑
	baffle(22),//障碍物
	//boss(25),//boss
	laddernpc(24),//天梯npc
	rolecopy(25),//角色分身
	;
	
	public final int type;
	
	public static boolean questEnable(int type){
		return type == npc.type ;
	}
	
	NpcType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	
	public static boolean isCorrectType(int type){
		return 1 == type
		|| 2 == type 
		|| 19 == type
		|| 22 == type ;
	}
}
