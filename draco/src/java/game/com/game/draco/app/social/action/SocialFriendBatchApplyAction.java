package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1213_SocialFriendBatchApplyReqMessage;
import com.game.draco.message.response.C1213_SocialFriendBatchApplyRespMessage;

public class SocialFriendBatchApplyAction extends BaseAction<C1213_SocialFriendBatchApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1213_SocialFriendBatchApplyReqMessage reqMsg) {
		C1213_SocialFriendBatchApplyRespMessage resp = new C1213_SocialFriendBatchApplyRespMessage();
		int[] roleIds = reqMsg.getRoleIds();
		if(null == roleIds || 0 == roleIds.length){
			resp.setType((byte) 0);
			resp.setInfo(Status.Social_Error.getTips());
			return resp;
		}
		//批量添加好友
		GameContext.getSocialApp().friendApply(this.getCurrentRole(context), roleIds);
		resp.setType((byte) 1);
		resp.setInfo(Status.Social_Friend_Apply_Wait.getTips());
		return resp;
	}
	
}
