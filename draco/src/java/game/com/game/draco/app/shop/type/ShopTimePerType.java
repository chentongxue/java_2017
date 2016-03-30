package com.game.draco.app.shop.type;

public enum ShopTimePerType {
	
	per_0(0,0),
	per_10(1,10),
	per_20(2,20),
	per_30(3,30),
	per_40(4,40),
	per_50(5,50),
	per_60(6,60),
	per_70(7,70),
	per_80(8,80),
	per_90(9,90),
	
	;
	
	private final int type;
	private final int per;
	
	ShopTimePerType(int type, int per){
		this.per = per;
		this.type = type;
	}
	
	public int getPer(){
		return this.per;
	}
	
	public int getType(){
		return this.type;
	}
	
	public static ShopTimePerType getShopTimePerType(int comparison){
		ShopTimePerType value = ShopTimePerType.per_0;
		for(ShopTimePerType per : values()){
			if(comparison >= per.getPer()){
				value = per;
			}
			else if(comparison < per.getPer()){
				return value;
			}
		}
		return per_0;
	}
}
