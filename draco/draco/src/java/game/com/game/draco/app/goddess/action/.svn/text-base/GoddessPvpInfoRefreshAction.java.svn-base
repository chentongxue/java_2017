package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1358_GoddessPvpInfoRefreshReqMessage;

public class GoddessPvpInfoRefreshAction extends BaseAction<C1358_GoddessPvpInfoRefreshReqMessage> {

	@Override
	public Message execute(ActionContext context, C1358_GoddessPvpInfoRefreshReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Result result = GameContext.getGoddessApp().refreshPvpInfoList(role);
		if(!result.isSuccess()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(result.getInfo());
			return tipMsg;
		}
		
		return null;
	}

}
