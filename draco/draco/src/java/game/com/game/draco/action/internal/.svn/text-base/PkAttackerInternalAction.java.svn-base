package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0082_PkAttackerInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class PkAttackerInternalAction extends BaseAction<C0082_PkAttackerInternalMessage>{

	@Override
	public Message execute(ActionContext context, C0082_PkAttackerInternalMessage reqMsg) {
		GameContext.getPkApp().internalAttackerLogic(reqMsg.getAttacker(), reqMsg.getVictim());
		return null;
	}
}
