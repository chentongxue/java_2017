package com.game.draco.app.quest.poker;

public enum PokerTwoType {
	
	Common(0,"普通"),
	DuiZi(1,"对子"),
	Link(2,"相连"),
	Gap(3,"间连"),
	TongHua(4,"同花"),
	TongHua_Link(5,"同花相连"),
	TongHua_Gap(6,"同花间连"),
	
	;
	
	private final int type;
	private final String name;

	PokerTwoType(int type, String name){
		this.type = type;
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static PokerTwoType get(int type){
		for(PokerTwoType item : PokerTwoType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
