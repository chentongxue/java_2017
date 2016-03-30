package com.game.draco.message.internal;

import lombok.Data;
import sacred.alliance.magic.app.trading.CancelReason;
import sacred.alliance.magic.app.trading.TradingMatch;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0061_TradingCancelUserExecInternalMessage extends InternalMessage{

	public C0061_TradingCancelUserExecInternalMessage(){
		this.commandId = 61 ;
	}
	
	private CancelReason reason  ;
	private TradingMatch match ;
	//年係捐
	private RoleInstance canceler ;
	private String rollbackRoleId ;
}
