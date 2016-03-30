package com.game.draco.app.qualify.domain;

public enum RankStatusType {

	keep((byte) 0, "不变"), 
	up((byte) 1, "上升"),
	down((byte) 2, "下降"),
	;

	private final byte type;
	private final String name;

	RankStatusType(byte type, String name) {
		this.name = name;
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static RankStatusType get(byte type) {
		for (RankStatusType item : RankStatusType.values()) {
			if (item.getType() == type) {
				return item;
			}
		}
		return null;
	}
}
