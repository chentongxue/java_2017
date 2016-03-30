package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1208_SocialBlackRemoveReqMessage;
import com.game.draco.message.response.C1208_SocialBlackRemoveRespMessage;

public class SocialBlackRemoveAction extends BaseAction<C1208_SocialBlackRemoveReqMessage> {

	@Override
	public Message execute(ActionContext context, C1208_SocialBlackRemoveReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int targRoleId = reqMsg.getRoleId();
		Result result = GameContext.getSocialApp().blackRemove(role, String.valueOf(targRoleId));
		C1208_SocialBlackRemoveRespMessage resp = new C1208_SocialBlackRemoveRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		resp.setRoleId(targRoleId);
		return resp;
	}
	
}
