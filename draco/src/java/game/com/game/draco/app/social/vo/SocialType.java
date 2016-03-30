package com.game.draco.app.social.vo;

public enum SocialType {

	Blacklist((byte) 1, "黑名单"), Friend((byte) 0, "好友");

	private final byte type;
	private final String name;

	SocialType(byte type, String name) {
		this.name = name;
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public static SocialType get(byte type) {
		for (SocialType item : SocialType.values()) {
			if (item.getType() == type) {
				return item;
			}
		}
		return null;
	}
}
