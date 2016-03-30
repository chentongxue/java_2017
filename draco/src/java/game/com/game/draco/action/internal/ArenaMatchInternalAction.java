package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0052_ArenaMatchInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class ArenaMatchInternalAction extends BaseAction<C0052_ArenaMatchInternalMessage>{
	@Override
	public Message execute(ActionContext context,
			C0052_ArenaMatchInternalMessage reqMsg) {
		//匹配
		GameContext.getArenaApp().systemMatch(reqMsg.getActiveId());
		return null;
	}

}
