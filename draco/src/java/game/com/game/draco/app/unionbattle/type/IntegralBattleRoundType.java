package com.game.draco.app.unionbattle.type;

import sacred.alliance.magic.constant.TextId;

public enum IntegralBattleRoundType {
	
	one(0,TextId.UNION_INTEGRAL_ONE),
	two(1,TextId.UNION_INTEGRAL_TWO),
	three(2,TextId.UNION_INTEGRAL_THREE),
	;
	
	private final int num;

	private final String info;
	
	public String getInfo() {
		return info;
	}

	public int getNum() {
		return num;
	}
	
	IntegralBattleRoundType(int num,String info){
		this.num = num;
		this.info = info;
	}
	
	public static IntegralBattleRoundType get(int num){
		for(IntegralBattleRoundType t : values()){
			if(t.getNum() == num){
				return t ;
			}
		}
		return null ;
	}
}
