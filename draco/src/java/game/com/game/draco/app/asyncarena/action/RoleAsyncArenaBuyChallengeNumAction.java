package com.game.draco.app.asyncarena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.AsyncArenaRoleBuyChallengeItem;
import com.game.draco.message.request.C2627_AsyncArenaBuyNumReqMessage;
import com.game.draco.message.response.C2627_AsyncArenaBuyNumRespMessage;

public class RoleAsyncArenaBuyChallengeNumAction extends BaseAction<C2627_AsyncArenaBuyNumReqMessage> {

	@Override
	public Message execute(ActionContext context, C2627_AsyncArenaBuyNumReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}

		return GameContext.getRoleAsyncArenaApp().buyChallengeNum(role);
	}

}
