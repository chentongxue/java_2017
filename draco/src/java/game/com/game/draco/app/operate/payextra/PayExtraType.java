package com.game.draco.app.operate.payextra;

public enum PayExtraType {

	
	pay_extra((byte) 0, "月卡"),
	month_card((byte) 1, "月卡"),
	grow_fund((byte) 2, "成长基金"),
	;

	private final byte type;
	private final String name;

	PayExtraType(byte type, String name) {
		this.name = name;
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static PayExtraType get(byte type) {
		for (PayExtraType item : PayExtraType.values()) {
			if (item.getType() == type) {
				return item;
			}
		}
		return null;
	}
}
