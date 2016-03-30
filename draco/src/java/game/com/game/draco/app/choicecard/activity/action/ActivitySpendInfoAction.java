package com.game.draco.app.choicecard.activity.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2812_CardActivityReqMessage;
import com.game.draco.message.response.C2812_CardActivityRespMessage;

public class ActivitySpendInfoAction extends BaseAction<C2812_CardActivityReqMessage> {

	@Override
	public Message execute(ActionContext context, C2812_CardActivityReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		C2812_CardActivityRespMessage respMsg = GameContext.getRoleChoiceCardApp().sendC2812_ActivityCardRespMessage(role);
		return respMsg;
	}

}
