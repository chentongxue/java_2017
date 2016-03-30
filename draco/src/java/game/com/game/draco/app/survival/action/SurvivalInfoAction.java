package com.game.draco.app.survival.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0282_SurvivalInfoReqMessage;

public class SurvivalInfoAction extends BaseAction<C0282_SurvivalInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C0282_SurvivalInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		return GameContext.getSurvivalBattleApp().sendC0282_SurvivalInfoRespMessage(role);
	}

}
