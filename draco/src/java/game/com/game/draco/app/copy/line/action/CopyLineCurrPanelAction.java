package com.game.draco.app.copy.line.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0270_CopyLineCurrPanelReqMessage;

public class CopyLineCurrPanelAction extends BaseAction<C0270_CopyLineCurrPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0270_CopyLineCurrPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getCopyLineApp().getCopyLinePanelRespMessage(role, (byte) 0);
	}
	
}
