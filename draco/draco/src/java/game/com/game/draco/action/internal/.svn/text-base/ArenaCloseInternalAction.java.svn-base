package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0053_ArenaCloseInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class ArenaCloseInternalAction extends BaseAction<C0053_ArenaCloseInternalMessage>{

	
	@Override
	public Message execute(ActionContext context,
			C0053_ArenaCloseInternalMessage reqMsg) {
		GameContext.getArenaApp().systemClose(reqMsg.getActiveId());
		return null;
	}

}
