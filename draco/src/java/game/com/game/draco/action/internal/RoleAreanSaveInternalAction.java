package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0079_RoleAreanSaveInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleAreanSaveInternalAction  extends BaseAction<C0079_RoleAreanSaveInternalMessage>{

	@Override
	public Message execute(ActionContext context,
			C0079_RoleAreanSaveInternalMessage reqMsg) {
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(reqMsg.getRoleId());
		if(null == role){
			return null ;
		}
		GameContext.getArenaApp().onLogout(role, null);
		return null ;
	}

}
