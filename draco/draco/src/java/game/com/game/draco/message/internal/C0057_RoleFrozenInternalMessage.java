package com.game.draco.message.internal;

import com.game.draco.debug.message.request.C10015_FrozenRoleReqMessage;

import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0057_RoleFrozenInternalMessage extends InternalMessage{

	public C0057_RoleFrozenInternalMessage(){
		this.commandId = 57 ;
	}
	
	private RoleInstance role ;
	private C10015_FrozenRoleReqMessage adminReqMsg ;
}
