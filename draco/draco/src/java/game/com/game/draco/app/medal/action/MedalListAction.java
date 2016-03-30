package com.game.draco.app.medal.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0521_MedalListReqMessage;

public class MedalListAction extends BaseAction<C0521_MedalListReqMessage>{

	@Override
	public Message execute(ActionContext context, C0521_MedalListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		return GameContext.getMedalApp().getC0521_MedalListRespMessage(role);
	}

}
