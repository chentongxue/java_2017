package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0071_RoleWarehouseMailInternalMessage extends InternalMessage{

	public C0071_RoleWarehouseMailInternalMessage(){
		this.commandId = 71 ;
	}
	
	private String roleId ;
}
