package com.game.draco.app.nostrum.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1912_NostrumUseReqMessage;

public class NostrumUseAction extends BaseAction<C1912_NostrumUseReqMessage> {

	@Override
	public Message execute(ActionContext context, C1912_NostrumUseReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		//使用秘药物品
		GameContext.getNostrumApp().useNostrum(role, reqMsg.getGoodsId());
		return null;
	}

}
