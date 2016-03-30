package com.game.draco.app.richman.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2654_RichManRoleArrivedReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RichManRoleArrivedAction extends BaseAction<C2654_RichManRoleArrivedReqMessage> {

	@Override
	public Message execute(ActionContext context, C2654_RichManRoleArrivedReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GameContext.getRichManApp().roleArrived(role);
		return null;
	}

}
