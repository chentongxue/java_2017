package com.game.draco.app.goddess.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1359_GoddessPvpReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoddessPvpAction extends BaseAction<C1359_GoddessPvpReqMessage> {

	@Override
	public Message execute(ActionContext context, C1359_GoddessPvpReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Result result = GameContext.getGoddessApp().challenge(role, reqMsg.getRoleId(), reqMsg.getRoleName()
				, reqMsg.getGoddessId(), reqMsg.getType());
		if(!result.isSuccess()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(result.getInfo());
			return tipMsg;
		}
		return null;
	}

}
