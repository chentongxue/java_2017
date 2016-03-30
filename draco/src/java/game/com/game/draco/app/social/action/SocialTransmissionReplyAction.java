package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1220_SocialTransmissionReplyReqMessage;
import com.game.draco.message.response.C1201_SocialFriendRespMessage;

public class SocialTransmissionReplyAction extends BaseAction<C1220_SocialTransmissionReplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1220_SocialTransmissionReplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		String targetRoleId = String.valueOf(reqMsg.getRoleId());
		Result result = GameContext.getSocialApp().transmissionReply(role, reqMsg.getType(), targetRoleId);
		C1201_SocialFriendRespMessage resp = new C1201_SocialFriendRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
