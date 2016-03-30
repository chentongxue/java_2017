package com.game.draco.action.internal;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0068_SurvivalTeamMatchInternalMessage;

public class SurvivalTeamMatchInternalAction extends BaseAction<C0068_SurvivalTeamMatchInternalMessage>{

	@Override
	public Message execute(ActionContext context, C0068_SurvivalTeamMatchInternalMessage reqMsg) {
		GameContext.getSurvivalBattleApp().systemMatch();
		return null ;
	}

}
