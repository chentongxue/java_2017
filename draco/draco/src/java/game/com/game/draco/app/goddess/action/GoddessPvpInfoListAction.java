package com.game.draco.app.goddess.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1357_GoddessPvpInfoListReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoddessPvpInfoListAction extends BaseAction<C1357_GoddessPvpInfoListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1357_GoddessPvpInfoListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GameContext.getGoddessApp().sendPvpInfoPanel(role, reqMsg.getType());
		return null;
	}

}
