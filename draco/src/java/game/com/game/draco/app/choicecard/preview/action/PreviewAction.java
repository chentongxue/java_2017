package com.game.draco.app.choicecard.preview.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2809_CardPreviewReqMessage;

public class PreviewAction extends BaseAction<C2809_CardPreviewReqMessage> {

	@Override
	public Message execute(ActionContext context, C2809_CardPreviewReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		return GameContext.getRoleChoiceCardApp().sendC2820_CardPreviewRespMessage(reqMsg.getType());
	}

}
