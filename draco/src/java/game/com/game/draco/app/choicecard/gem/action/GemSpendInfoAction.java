package com.game.draco.app.choicecard.gem.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2811_CardGemReqMessage;
import com.game.draco.message.response.C2811_CardGemRespMessage;

public class GemSpendInfoAction extends BaseAction<C2811_CardGemReqMessage> {

	@Override
	public Message execute(ActionContext context, C2811_CardGemReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2811_CardGemRespMessage respMsg = GameContext.getRoleChoiceCardApp().sendC2811_GemCardRespMessage(role);
		return respMsg;
	}

}
