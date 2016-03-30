package com.game.draco.app.luckybox.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1916_LuckyBoxPlayReqMessage;
/**
 * 
 */
public class LuckyBoxPlayAction  extends BaseAction<C1916_LuckyBoxPlayReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1916_LuckyBoxPlayReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getLuckyBoxApp().playLuckyBox(role);
	}

	
}
