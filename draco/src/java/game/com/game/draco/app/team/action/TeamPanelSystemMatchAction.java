package com.game.draco.app.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0069_TeamPanelMatchInternalMessage;

public class TeamPanelSystemMatchAction extends BaseAction<C0069_TeamPanelMatchInternalMessage> {

	@Override
	public Message execute(ActionContext context, C0069_TeamPanelMatchInternalMessage reqMsg) {
		GameContext.getTeamApp().systemMatch();
		return null;
	}

}
