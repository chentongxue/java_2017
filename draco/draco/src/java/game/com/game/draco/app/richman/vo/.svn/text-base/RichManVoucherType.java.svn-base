package com.game.draco.app.richman.vo;

public enum RichManVoucherType {
	value((byte)0, "value"),
	Percent((byte)1, "percent"),
	;
	
	private final byte type;
	private final String name;
	
	RichManVoucherType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static RichManVoucherType get(byte type) {
		for(RichManVoucherType tt : RichManVoucherType.values()) {
			if(type != tt.getType()) {
				continue;
			}
			return tt;
		}
		return null;
	}
	
}
