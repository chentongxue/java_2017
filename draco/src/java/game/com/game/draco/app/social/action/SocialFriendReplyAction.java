package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1203_SocialFriendReplyReqMessage;
import com.game.draco.message.response.C1201_SocialFriendRespMessage;

public class SocialFriendReplyAction extends BaseAction<C1203_SocialFriendReplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1203_SocialFriendReplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1201_SocialFriendRespMessage resp = new C1201_SocialFriendRespMessage();
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(reqMsg.getRoleId()));
		if (null == targetRole) {
			resp.setInfo(Status.Social_TargRole_Offline.getTips());
			return resp;
		}
		Result result = GameContext.getSocialApp().friendReply(role, reqMsg.getType(), targetRole);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
