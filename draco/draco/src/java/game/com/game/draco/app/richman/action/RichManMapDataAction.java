package com.game.draco.app.richman.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2661_RichManMapDataReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RichManMapDataAction extends BaseAction<C2661_RichManMapDataReqMessage> {

	@Override
	public Message execute(ActionContext context, C2661_RichManMapDataReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GameContext.getRichManApp().mapGetDataAndEnter(role);
		return null;
	}

}
