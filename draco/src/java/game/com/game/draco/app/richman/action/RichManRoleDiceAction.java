package com.game.draco.app.richman.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2652_RichManRoleDiceReqMessage;

public class RichManRoleDiceAction extends BaseAction<C2652_RichManRoleDiceReqMessage> {

	@Override
	public Message execute(ActionContext context, C2652_RichManRoleDiceReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Result result = GameContext.getRichManApp().roleDice(role, reqMsg.getParams());
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(result.getInfo());
			return tipMsg;
		}
		return null;
	}

}
