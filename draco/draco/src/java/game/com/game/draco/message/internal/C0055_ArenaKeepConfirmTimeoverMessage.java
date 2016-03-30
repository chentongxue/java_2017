package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.app.arena.ArenaMatch;

public @Data class C0055_ArenaKeepConfirmTimeoverMessage extends InternalMessage{

	public C0055_ArenaKeepConfirmTimeoverMessage(){
		this.commandId = 55 ;
	}
	private ArenaMatch match ;
}
