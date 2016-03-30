package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0082_PkAttackerInternalMessage extends InternalMessage{

	public C0082_PkAttackerInternalMessage(){
		this.commandId = 82 ;
	}
	
	private RoleInstance attacker;
	private RoleInstance victim;
}
