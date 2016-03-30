package com.game.draco.app.unionbattle.type;

public enum IntegralBattleType {
	
	one(1,5,1),
	two(2,2,2),
	three(3,1,5),
	;
	
	private final int num;

	private final int value;
	
	private final int otherValue;

	public int getOtherValue() {
		return otherValue;
	}

	public int getNum() {
		return num;
	}
	
	public int getValue() {
		return value;
	}

	IntegralBattleType(int num,int value,int otherValue){
		this.num = num;
		this.value = value;
		this.otherValue = otherValue;
	}
	
	public static IntegralBattleType get(int num){
		for(IntegralBattleType t : values()){
			if(t.getNum() == num){
				return t ;
			}
		}
		return null ;
	}
}
