package com.game.draco.app.choicecard.activity.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.choicecard.ChoiceFunType;
import com.game.draco.message.request.C2815_CardActivitySpendReqMessage;
import com.game.draco.message.response.C2815_SpendActivityRespMessage;

public class ActivitySpendAction extends BaseAction<C2815_CardActivitySpendReqMessage> {

	@Override
	public Message execute(ActionContext context, C2815_CardActivitySpendReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Result result = GameContext.getRoleChoiceCardApp().choiceCard(role, (byte)ChoiceFunType.ACTIVITY.ordinal(), reqMsg.getSpecificType());
		if(result.isIgnore()){
			return null;
		}
		C2815_SpendActivityRespMessage respMsg = new C2815_SpendActivityRespMessage();
		respMsg.setSuccess(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg;
	}

}
