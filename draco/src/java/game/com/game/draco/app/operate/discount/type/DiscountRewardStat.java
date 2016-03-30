package com.game.draco.app.operate.discount.type;

public enum DiscountRewardStat {
	REWARD_CANNOT(0), //不能领
	REWARD_DONE(1),//已领取
	REWARD_CAN(2); //能领
	
	private final int type;
	DiscountRewardStat(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static DiscountRewardStat get(int type){
		for(DiscountRewardStat v : values()){
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
}
