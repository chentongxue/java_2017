package com.game.draco.app.accumulatelogin.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2522_AccumulateLoginAwardDetailReqMessage;
/**
 * 
 */
public class AccumulateLoginAwardDetailAction  extends BaseAction<C2522_AccumulateLoginAwardDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C2522_AccumulateLoginAwardDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		byte day = reqMsg.getDay();
		return GameContext.getAccumulateLoginApp().getAccumulateLoginAwardDetail(role, day);
	}
}
