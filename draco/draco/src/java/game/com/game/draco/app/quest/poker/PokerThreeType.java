package com.game.draco.app.quest.poker;

public enum PokerThreeType {
	
	Common(0,"普通"),
	DuiZi(1,"对子"),
	TongHua(2,"同花"),
	ShunZi(3,"顺子"),
	TongHuaShun(4,"同花顺"),
	BaoZi(5,"豹子"),
	
	;
	
	private final int type;
	private final String name;

	PokerThreeType(int type, String name){
		this.type = type;
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public static PokerThreeType get(int type){
		for(PokerThreeType item : PokerThreeType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
