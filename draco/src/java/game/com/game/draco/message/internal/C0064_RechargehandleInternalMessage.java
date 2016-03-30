package com.game.draco.message.internal;

import lombok.Data;
import platform.message.request.C5901_ChargeNotifyReqMessage;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0064_RechargehandleInternalMessage extends InternalMessage{

	public C0064_RechargehandleInternalMessage(){
		this.commandId = 64 ;
	}
	
	private ChannelSession originalSession ;
	private C5901_ChargeNotifyReqMessage originalMessage;

}
