package com.game.draco.app.compass.action;


import com.game.draco.GameContext;
import com.game.draco.message.request.C1908_CompassDisplayReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CompassDisplayAction extends BaseAction<C1908_CompassDisplayReqMessage> {

	@Override
	public Message execute(ActionContext context, C1908_CompassDisplayReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getCompassApp().openCompassPanel(role, reqMsg.getId());
	}

}
