package com.game.draco.app.copy.line.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0272_CopyLineEnterReqMessage;

public class CopyLineEnterAction extends BaseAction<C0272_CopyLineEnterReqMessage>{

	@Override
	public Message execute(ActionContext context, C0272_CopyLineEnterReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		GameContext.getCopyLineApp().enterCopy(role, reqMsg.getCopyId());
		return null;
	}
	
}
