package com.game.draco.action.internal;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0087_CampWarMatchInternalMessage;

public class CampWarMatchInternalAction extends BaseAction<C0087_CampWarMatchInternalMessage> {

	@Override
	public Message execute(ActionContext context,
			C0087_CampWarMatchInternalMessage reqMsg) {
		GameContext.getCampWarApp().doApplyMatch();
		return null ;
	}

}
