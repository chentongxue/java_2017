package com.game.draco.message.internal;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.domain.TitleRecord;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0070_TitleTimeoutExecInternalMessage extends InternalMessage{

	public C0070_TitleTimeoutExecInternalMessage(){
		this.commandId = -70 ;
	}
	
	private RoleInstance role ;
	private List<TitleRecord> timeoutList ;
}
