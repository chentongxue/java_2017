package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0066_CopyTeamMatchInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class CopyTeamMatchInternalAction extends BaseAction<C0066_CopyTeamMatchInternalMessage>{

	@Override
	public Message execute(ActionContext context, C0066_CopyTeamMatchInternalMessage reqMsg) {
		GameContext.getCopyTeamApp().systemMatch();
		return null ;
	}

}
