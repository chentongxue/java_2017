package com.game.draco.app.copy.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0210_CopyEnterConfirmReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyEnterConfirmAction extends BaseAction<C0210_CopyEnterConfirmReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0210_CopyEnterConfirmReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		GameContext.getCopyLogicApp().teamCopyCreateConfirm(role, reqMsg.getParam());
		return null;
	}
}
