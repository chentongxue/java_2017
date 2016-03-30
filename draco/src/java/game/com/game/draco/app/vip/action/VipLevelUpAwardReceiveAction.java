package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;
import com.game.draco.GameContext;
import com.game.draco.message.request.C2513_VipLevelUpAwardReceiveReqMessage;
public class VipLevelUpAwardReceiveAction  extends BaseAction<C2513_VipLevelUpAwardReceiveReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2513_VipLevelUpAwardReceiveReqMessage msg) {
		RoleInstance role = this.getCurrentRole(ct);
		byte vipLevel = msg.getVipLevel();
		return GameContext.getVipApp().receiveVipLevelUpAward(role, vipLevel);
	}

}
