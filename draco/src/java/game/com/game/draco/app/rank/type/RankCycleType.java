package com.game.draco.app.rank.type;

public enum RankCycleType {

	Forever(0),
	Day(1),
	WEEK(2),
	;

	private final int cycle ;
	private RankCycleType(int cycle){
		this.cycle = cycle ;
	}

	public int getCycle() {
		return cycle;
	}
	
	
}
