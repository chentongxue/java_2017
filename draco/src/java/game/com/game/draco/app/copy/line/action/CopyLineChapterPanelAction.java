package com.game.draco.app.copy.line.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0271_CopyLineChapterPanelReqMessage;

public class CopyLineChapterPanelAction extends BaseAction<C0271_CopyLineChapterPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0271_CopyLineChapterPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getCopyLineApp().getCopyLinePanelRespMessage(role, reqMsg.getChapterId());
	}
	
}
