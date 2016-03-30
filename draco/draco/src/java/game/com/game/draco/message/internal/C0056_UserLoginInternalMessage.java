package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.app.token.AccountToken;

import com.game.draco.message.request.C4999_UserLoginSafeReqMessage;

public @Data class C0056_UserLoginInternalMessage extends InternalMessage{

	public C0056_UserLoginInternalMessage(){
		this.commandId = 56 ;
		this.canRefuse = true ;
	}
	
	private String userId ;
	private C4999_UserLoginSafeReqMessage userReqMsg ;
	private AccountToken accountToken ;
}
