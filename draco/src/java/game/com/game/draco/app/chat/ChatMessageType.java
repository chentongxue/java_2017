package com.game.draco.app.chat;

public enum ChatMessageType {

	Text(0),
	Voice(1),


	;

	private final int type;

	ChatMessageType(int type){
		this.type = type;
	}

	public int getType() {
		return type;
	}


	
}
