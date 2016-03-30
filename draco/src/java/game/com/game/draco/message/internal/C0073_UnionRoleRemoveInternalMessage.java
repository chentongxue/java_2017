package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0073_UnionRoleRemoveInternalMessage extends InternalMessage{

	public C0073_UnionRoleRemoveInternalMessage(){
		this.commandId = 73 ;
	}
	
	private String roleId ;
}
