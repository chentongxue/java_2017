package com.game.draco.app.shop.type;

public enum ShopType {
	
	FlashSale((byte)0,true,"限时抢购"),
	DailyLimit((byte)1,true,"每日限购"),
	;
	
	private final byte type;
	private final boolean goldMoney;//是否钻石购买
	private final String name;
	
	ShopType(byte type, boolean goldMoney, String name){
		this.type = type;
		this.goldMoney = goldMoney;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static ShopType get(byte type){
		for(ShopType item : ShopType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}

	public boolean isGoldMoney() {
		return goldMoney;
	}
	
}
