package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0052_ArenaMatchInternalMessage extends InternalMessage{

	public C0052_ArenaMatchInternalMessage(){
		this.commandId = 52 ;
	}
	
	private int activeId ;
	
}
