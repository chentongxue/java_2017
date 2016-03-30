package com.game.draco.app.chat;

public enum ChatShowType {

	team((short) 1312, "发布组队申请"), 
	good((short) 508, "查看物品详情");

	private final short id;
	private final String name;

	ChatShowType(short id, String name) {
		this.id = id;
		this.name = name;
	}

	public short getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static ChatShowType getChatShowType(short id) {
		for (ChatShowType type : ChatShowType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}

}
