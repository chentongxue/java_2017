package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1207_SocialBlackApplyReqMessage;
import com.game.draco.message.response.C1207_SocialBlackApplyRespMessage;

public class SocialBlackApplyAction extends BaseAction<C1207_SocialBlackApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1207_SocialBlackApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1207_SocialBlackApplyRespMessage resp = new C1207_SocialBlackApplyRespMessage();
		int targetRoleId = reqMsg.getRoleId();
		String targetRoleName = reqMsg.getRoleName();
		RoleInstance targetRole = null;
		if (-1 == targetRoleId) {
			if (targetRoleName.equals(role.getRoleName())) {
				resp.setInfo(Status.Social_Black_Not_Self.getTips());
				return resp;
			}
			targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleName(targetRoleName);
		} else {
			if (role.getIntRoleId() == targetRoleId) {
				resp.setInfo(Status.Social_Black_Not_Self.getTips());
				return resp;
			}
			targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(targetRoleId));
		}
		if (null == targetRole) {
			resp.setInfo(Status.Social_TargRole_Offline.getTips());
			return resp;
		}
		Result result = GameContext.getSocialApp().blackApply(role, targetRole);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
