package com.game.draco.app.compass.action;
import com.game.draco.GameContext;
import com.game.draco.message.request.C1910_CompassStopReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CompassStopAction extends BaseAction<C1910_CompassStopReqMessage> {

	@Override
	public Message execute(ActionContext context, C1910_CompassStopReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		GameContext.getCompassApp().compassStop(role, reqMsg.getId());
		return null;
	}

}
