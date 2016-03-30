package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1218_SocialTransmissionApplyReqMessage;
import com.game.draco.message.response.C1218_SocialTransmissionRespMessage;

public class SocialTransmissionApplyAction extends BaseAction<C1218_SocialTransmissionApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1218_SocialTransmissionApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int targetRoleId = reqMsg.getRoleId();
		C1218_SocialTransmissionRespMessage resp = new C1218_SocialTransmissionRespMessage();
		if (role.getIntRoleId() == targetRoleId) {
			resp.setInfo(Status.Social_Transmission_Self.getTips());
			return resp;
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(targetRoleId));
		if (null == targetRole) {
			resp.setInfo(Status.Social_TargRole_Offline.getTips());
			return resp;
		}
		Result result = GameContext.getSocialApp().transmissionApply(role, targetRole);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
