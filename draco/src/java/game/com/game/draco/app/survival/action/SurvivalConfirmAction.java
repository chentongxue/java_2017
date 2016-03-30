package com.game.draco.app.survival.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0281_SurvivalConfirmReqMessage;

public class SurvivalConfirmAction extends BaseAction<C0281_SurvivalConfirmReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0281_SurvivalConfirmReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		GameContext.getSurvivalBattleApp().survivalTeamConfirm(role, reqMsg.getParam());
		return null;
	}
}
