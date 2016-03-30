package com.game.draco.app.forward.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1123_ForwardReqMessage;

public class ForwardAction extends BaseAction<C1123_ForwardReqMessage> {

	@Override
	public Message execute(ActionContext context,	C1123_ForwardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GameContext.getForwardApp().forward(role, reqMsg.getForwardId());
		return null;
	}

}
