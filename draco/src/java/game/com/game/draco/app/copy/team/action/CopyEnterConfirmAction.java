package com.game.draco.app.copy.team.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0223_CopyEnterConfirmReqMessage;

public class CopyEnterConfirmAction extends BaseAction<C0223_CopyEnterConfirmReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0223_CopyEnterConfirmReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		GameContext.getCopyTeamApp().copyTeamConfirm(role, reqMsg.getParam());
		return null;
	}
}
