package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1214_SocialDescReqMessage;
import com.game.draco.message.response.C1214_SocialDescRespMessage;

public class SocialDescAction extends BaseAction<C1214_SocialDescReqMessage> {

	@Override
	public Message execute(ActionContext context, C1214_SocialDescReqMessage reqMsg) {
		C1214_SocialDescRespMessage resp = new C1214_SocialDescRespMessage();
		resp.setDesc(GameContext.getSocialApp().getSocialDesc());
		return resp;
	}
	
}
