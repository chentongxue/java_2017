package com.game.draco.app.richman.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2650_RichManMapEnterReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RichManMapEnterAction extends BaseAction<C2650_RichManMapEnterReqMessage> {

	@Override
	public Message execute(ActionContext context, C2650_RichManMapEnterReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		Result result = GameContext.getRichManApp().enterMap(role);
		if(!result.isSuccess()){
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(result.getInfo());
			return msg;
		}
		return null;
	}

}
