package com.game.draco.app.role.systemset.vo;

/**
 * 
 * 组队屏蔽类型
 * [0:开启-正常组队 1:屏蔽-不可被组队 2:自动-自动接受组队]
 * @author dongrui
 *
 */
public enum TeamShieldType {
	
	Open(0,"打开"),
	Shield(1,"屏蔽"),
	Auto(2,"自动"),
	
	;
	
	private final int type;
	private final String name;
	
	TeamShieldType(int type,String name){
		this.type = type;
		this.name = name;
	}
	
	public int getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static TeamShieldType get(int type){
		for(TeamShieldType item : TeamShieldType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
