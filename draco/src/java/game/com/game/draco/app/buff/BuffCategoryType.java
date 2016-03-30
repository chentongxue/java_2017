package com.game.draco.app.buff;

public enum BuffCategoryType {

	buff(0),//增益
	debuff(1),//减益
	moneyBuff(2),//货币buff
	teamBuff(3),//组队buff
	permanent(4);//永久buff
	
	private final int type;
	
	BuffCategoryType(int type){
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public static BuffCategoryType get(int type){
		for(BuffCategoryType t : values()){
			if(type == t.getType()){
				return t ;
			}
		}
		return buff ;
	}
	
}
