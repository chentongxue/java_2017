package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0070_TitleTimeoutExecInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class TitleTimeoutExecInternalAction extends BaseAction<C0070_TitleTimeoutExecInternalMessage>{

	@Override
	public Message execute(ActionContext context,
			C0070_TitleTimeoutExecInternalMessage reqMsg) {
		GameContext.getTitleApp().timeoutExec(reqMsg.getRole(),
				reqMsg.getTimeoutList());
		return null ;
	}

}
