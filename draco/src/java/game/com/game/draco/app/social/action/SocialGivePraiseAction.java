package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1215_SocialPraiseReqMessage;
import com.game.draco.message.response.C1215_SocialPraiseRespMessage;

public class SocialGivePraiseAction extends BaseAction<C1215_SocialPraiseReqMessage> {

	@Override
	public Message execute(ActionContext context, C1215_SocialPraiseReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int targRoleId = reqMsg.getRoleId();
		C1215_SocialPraiseRespMessage resp = new C1215_SocialPraiseRespMessage();
		RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(targRoleId));
		if (null == targRole) {
			resp.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		Result result = GameContext.getSocialApp().givePraise(role, targRole);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
