package com.game.draco.message.internal;

import lombok.Data;

public @Data class C0086_UnionAddMemberMessage extends InternalMessage{

	public C0086_UnionAddMemberMessage(){
		this.commandId = 86;
	}
	
	private int roleId ;
	
	//操作者ID(发消息用)
	private int operaRoleId;
}
