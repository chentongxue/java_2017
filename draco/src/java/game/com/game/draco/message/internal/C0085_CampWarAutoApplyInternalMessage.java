package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0085_CampWarAutoApplyInternalMessage extends InternalMessage{

	public C0085_CampWarAutoApplyInternalMessage(){
		this.commandId = 85 ;
	}
	
	private RoleInstance role ;
	private boolean apply  = true ;
}
