package com.game.draco.action.internal;

import sacred.alliance.magic.app.arena.action.Arena1V1AbstractAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0075_Arena1V1AutoApplyInternalMessage;

public class Arena1V1AutoApplyInternalAction 
	extends Arena1V1AbstractAction<C0075_Arena1V1AutoApplyInternalMessage>{

	
	@Override
	public Message execute(ActionContext context,
			C0075_Arena1V1AutoApplyInternalMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		if(GameContext.getArena1V1App().isAcitveTimes()){
			return this.buildIngDetailRespMessage(role,true);
		}
		return null ;
	}

}
