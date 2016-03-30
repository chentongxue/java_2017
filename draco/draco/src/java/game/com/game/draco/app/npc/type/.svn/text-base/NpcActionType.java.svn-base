package com.game.draco.app.npc.type;

public enum NpcActionType {

	ANIMAL(0,"动物"),//动物,随机移动
	GUARD(1,"卫兵"),//不动，但有固定点
	PATROLMAN(2,"巡逻者"),//固定线路行走
	ROOT(3,"站桩"),//坚决不能动
	;
	
	private final int type;
	private final String name ;
	
	NpcActionType(int type,String name){
			this.type = type;
			this.name = name ;
	}
	
	public int getType(){
		return type;
	}
	
	public static NpcActionType getType(int type){
		for(NpcActionType v : values()){
			if(v.getType() == type){
				return v ;
			}
		}
		return ROOT ;
	}
	
	public static String toString(int type){
		for(NpcActionType v : values()){
			if(v.getType() == type){
				return v.getName() ;
			}
		}
		return ROOT.getName() ;
	}

	public String getName() {
		return name;
	}
	
	
}
