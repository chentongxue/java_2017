package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2512_VipDailyAwardReceiveReqMessage;
/**
 * 
 */
public class VipDailyAwardReceiveAction  extends BaseAction<C2512_VipDailyAwardReceiveReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2512_VipDailyAwardReceiveReqMessage msg) {
		RoleInstance role = this.getCurrentRole(ct);
		return GameContext.getVipApp().vipDailyAwardReceive(role);
	}

}
