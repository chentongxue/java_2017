package com.game.draco.app.richman.vo;

public enum RichManEventType {
	
	None((byte)0, "none"),
	CouponMul((byte)1, "coupon mul or div"),
	CouponAdd((byte)2, "coupon add or sub"),
	Move((byte)3, "forwad ro back"),
	Trun((byte)4, "trun"),
	GetCard((byte)5, "get a card"),
	GetBox((byte)6, "get a box"),
	GodWealth((byte)7, "god of wealth"),
	GodBadLuck((byte)8, "god of bad luck"),
	BeAttacked((byte)9, "be attacked by card"),
	;
	
	private final byte type;
	private final String name;
	
	RichManEventType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public static RichManEventType get(byte type) {
		for(RichManEventType rmet : RichManEventType.values()) {
			if(type != rmet.getType()) {
				continue;
			}
			return rmet;
		}
		return null;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
}
