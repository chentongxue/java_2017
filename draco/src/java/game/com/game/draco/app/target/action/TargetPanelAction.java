package com.game.draco.app.target.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1121_TargetPanelReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TargetPanelAction extends BaseAction<C1121_TargetPanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C1121_TargetPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		return GameContext.getTargetApp().createTargetPanelMessage(role);
	}
	
}
