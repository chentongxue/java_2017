package com.game.draco.app.levelgift.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2402_LevelGiftListReqMessage;

public class LevelGiftListAction extends BaseAction<C2402_LevelGiftListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2402_LevelGiftListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getLevelGiftApp().getLevelGiftListMessage(role);
	}
}
