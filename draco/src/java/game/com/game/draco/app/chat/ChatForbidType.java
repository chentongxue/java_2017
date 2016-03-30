package com.game.draco.app.chat;

public enum ChatForbidType {
	
	None(0),//不禁言
	All(1),//全部禁言
	WordAndCamp(2),//禁言世界和阵营频道
	
	;
	
	private final int type;
	
	ChatForbidType(int type){
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static ChatForbidType getChatForbidType(int type){
		for(ChatForbidType item : ChatForbidType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return None;
	}
	
}
