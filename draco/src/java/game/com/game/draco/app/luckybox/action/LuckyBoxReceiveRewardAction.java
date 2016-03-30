package com.game.draco.app.luckybox.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1917_LuckyBoxReceiveAwardMessage;
/**
 * 
 */
public class LuckyBoxReceiveRewardAction  extends BaseAction<C1917_LuckyBoxReceiveAwardMessage>{

	@Override
	public Message execute(ActionContext context,
			C1917_LuckyBoxReceiveAwardMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getLuckyBoxApp().clearRewards (role);
	}

	
}
