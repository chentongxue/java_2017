package com.game.draco.app.operate.vo;

public enum OperateAwardType {
	can_receive((byte) 1, "可领取"),
	default_receive((byte) 2, "默认"),
	have_receive((byte) 3, "已领取"), 
	;

	private final byte type;
	private final String name;

	OperateAwardType(byte type, String name) {
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
