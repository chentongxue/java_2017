package com.game.draco.message.internal;

import lombok.Data;


public @Data class C0051_RoleTimingSaveInternalMessage extends InternalMessage{

	public C0051_RoleTimingSaveInternalMessage(){
		this.commandId = 51 ;
	}
	private String userId ;
	private String roleId ;
}
