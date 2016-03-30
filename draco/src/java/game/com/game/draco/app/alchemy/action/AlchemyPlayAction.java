package com.game.draco.app.alchemy.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1914_AlchemyPlayReqMessage;
/**
 * 
 */
public class AlchemyPlayAction  extends BaseAction<C1914_AlchemyPlayReqMessage>{

	@Override
	public Message execute(ActionContext context, C1914_AlchemyPlayReqMessage messge) {
		RoleInstance role = this.getCurrentRole(context);
		byte rewardType = messge.getRewardType();
		return GameContext.getAlchemyApp().getAlchemyResult(role,rewardType);
	}
	
}
