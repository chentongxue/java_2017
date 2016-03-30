package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1662_PetPvpInfoListReqMessage;

public class PetPvpInfoListAction extends BaseAction<C1662_PetPvpInfoListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1662_PetPvpInfoListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GameContext.getPetApp().sendPvpInfo(role, reqMsg.getType(), false);
		return null;
	}

}
