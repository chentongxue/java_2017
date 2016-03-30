package com.game.draco.app.operate.donate.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2395_ActiveDonateRankRewardReqMessage;

public class ActiveDonateRankRewardAction extends BaseAction<C2395_ActiveDonateRankRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2395_ActiveDonateRankRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		return GameContext.getDonateApp().recvRankReward(role, reqMsg.getActiveId());
	}

}
