package com.game.draco.app.choicecard.gem.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.choicecard.ChoiceFunType;
import com.game.draco.message.request.C2814_CardGemSpendReqMessage;
import com.game.draco.message.response.C2814_SpendGemRespMessage;

public class GemSpendAction extends BaseAction<C2814_CardGemSpendReqMessage> {

	@Override
	public Message execute(ActionContext context, C2814_CardGemSpendReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		Result result = GameContext.getRoleChoiceCardApp().choiceCard(role, (byte)ChoiceFunType.GEM.ordinal(), reqMsg.getSpecificType());
		C2814_SpendGemRespMessage respMsg = new C2814_SpendGemRespMessage();
		respMsg.setSuccess(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg;
	}

}
