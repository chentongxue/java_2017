package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2510_VipDisplayReqMessage;

public class VipDisplayAction  extends BaseAction<C2510_VipDisplayReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2510_VipDisplayReqMessage msg) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		return GameContext.getVipApp().openVipPanel(role);
	}
}
