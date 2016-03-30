package com.game.draco.app.union.battle.type;

public enum UnionBattleResultType {
	Lose((byte)0,"失败"),
	Win((byte)1,"胜利"),
	;
	
	private final byte type;
	private final String name;
	
	UnionBattleResultType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
}
