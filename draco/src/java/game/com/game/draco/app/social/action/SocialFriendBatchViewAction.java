package com.game.draco.app.social.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1212_SocialFriendBatchViewReqMessage;

public class SocialFriendBatchViewAction extends BaseAction<C1212_SocialFriendBatchViewReqMessage> {

	@Override
	public Message execute(ActionContext context, C1212_SocialFriendBatchViewReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getSocialApp().pushFriendBatchView(role);
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		return null;
	}
	
}
