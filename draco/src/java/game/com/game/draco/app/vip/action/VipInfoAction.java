package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2516_VipInfoReqMessage;

public class VipInfoAction  extends BaseAction<C2516_VipInfoReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2516_VipInfoReqMessage msg) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		return GameContext.getVipApp().getVipInfo(role);
	}
}
