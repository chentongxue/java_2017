package com.game.draco.app.tower.type;


public enum TowerStarConditionType {
	TIME_USED((byte)1,"时间"),
	HEROS_HP((byte)2,"英雄血量"),
	HERO_SERIES_LIMIT((byte)3,"英雄元素限制"),
	HERO_SWITCH_TIMES((byte)4,"英雄切换限制"),
	;
	
	private final byte type;
	private final String name;
	
	private TowerStarConditionType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	public static TowerStarConditionType getType(byte type) {
		for(TowerStarConditionType tp : TowerStarConditionType.values()){
			if(tp.getType() == type){
				return tp;
			}
		}
		return null ;
	}
}