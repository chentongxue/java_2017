package com.game.draco.app.operate.donate.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2392_ActiveDonateRankReqMessage;

public class ActiveDonateRankAction extends BaseAction<C2392_ActiveDonateRankReqMessage> {

	@Override
	public Message execute(ActionContext context, C2392_ActiveDonateRankReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		return GameContext.getDonateApp().createRankDetailMsg(role, reqMsg.getActiveId());
	}

}
