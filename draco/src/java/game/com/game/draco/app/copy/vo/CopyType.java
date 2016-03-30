package com.game.draco.app.copy.vo;

public enum CopyType {

	personal((byte) 0),//普通副本
	hero((byte) 1),//英雄副本
	team((byte) 2),//组队副本
	;

	private final byte type;

	CopyType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static CopyType get(byte type) {
		for (CopyType ct : CopyType.values()) {
			if (ct.getType() == type) {
				return ct;
			}
		}
		return null;
	}

}
