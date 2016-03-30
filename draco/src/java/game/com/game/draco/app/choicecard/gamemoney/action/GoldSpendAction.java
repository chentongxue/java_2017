package com.game.draco.app.choicecard.gamemoney.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.choicecard.ChoiceFunType;
import com.game.draco.message.request.C2813_CardGoldSpendReqMessage;
import com.game.draco.message.response.C2813_SpendGoldRespMessage;

public class GoldSpendAction extends BaseAction<C2813_CardGoldSpendReqMessage> {

	@Override
	public Message execute(ActionContext context, C2813_CardGoldSpendReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		Result result = GameContext.getRoleChoiceCardApp().choiceCard(role, (byte)ChoiceFunType.GOLD.ordinal(), reqMsg.getSpecificType());
		C2813_SpendGoldRespMessage respMsg = new C2813_SpendGoldRespMessage();
		respMsg.setSuccess(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg;
	}

}
