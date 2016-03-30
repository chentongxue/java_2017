package com.game.draco.app.shop.type;

import sacred.alliance.magic.base.AttributeType;

public enum ShopMoneyType {
	
	GoldMoney((byte)0,AttributeType.goldMoney.getName()),
	BindMoney((byte)1,AttributeType.bindingGoldMoney.getName()),
	
	;
	
	private final byte type;
	private final String name;
	
	ShopMoneyType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static ShopMoneyType get(byte type){
		for(ShopMoneyType item : ShopMoneyType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
