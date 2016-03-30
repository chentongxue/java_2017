package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleInstance;


public @Data class C0050_RoleOfflineInternalMessage extends InternalMessage{

	public C0050_RoleOfflineInternalMessage(){
		this.commandId = 50 ;
	}
	
	private String userId ;
	private ChannelSession session ;
	private RoleInstance role ;
	
}
