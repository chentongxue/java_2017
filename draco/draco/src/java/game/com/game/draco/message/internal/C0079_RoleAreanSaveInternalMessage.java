package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0079_RoleAreanSaveInternalMessage extends InternalMessage{

	public C0079_RoleAreanSaveInternalMessage(){
		this.commandId = 79 ;
	}
	
	private String roleId ;
}
