package com.game.draco.app.choicecard.gamemoney.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2810_CardGoldReqMessage;
import com.game.draco.message.response.C2810_CardGoldRespMessage;

public class GoldSpendInfoAction extends BaseAction<C2810_CardGoldReqMessage> {

	@Override
	public Message execute(ActionContext context, C2810_CardGoldReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2810_CardGoldRespMessage respMsg = GameContext.getRoleChoiceCardApp().sendC2810_GoldCardRespMessage(role);
		return respMsg;
	}

}
