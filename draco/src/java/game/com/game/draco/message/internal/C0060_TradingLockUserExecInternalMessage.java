package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.app.trading.TradingMatch;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0060_TradingLockUserExecInternalMessage extends InternalMessage {

	public C0060_TradingLockUserExecInternalMessage(){
		this.commandId = 60 ;
	}
	
	private RoleInstance role ;
	private TradingMatch match ;
	private int money ;
	private String[] goods ;
}
