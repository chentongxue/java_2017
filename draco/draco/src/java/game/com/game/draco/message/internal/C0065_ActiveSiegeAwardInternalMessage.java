package com.game.draco.message.internal;

import java.util.Collection;

import lombok.Data;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class C0065_ActiveSiegeAwardInternalMessage extends InternalMessage{

	public C0065_ActiveSiegeAwardInternalMessage(){
		this.commandId = 65 ;
	}
	
	private boolean success ;
	private Collection<RoleInstance> roleList;
	private int currHp;
	private int maxHp;
	private Active active;
	
}
