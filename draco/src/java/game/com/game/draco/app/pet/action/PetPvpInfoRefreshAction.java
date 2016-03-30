package com.game.draco.app.pet.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1663_PetPvpInfoRefreshReqMessage;

public class PetPvpInfoRefreshAction extends BaseAction<C1663_PetPvpInfoRefreshReqMessage> {

	@Override
	public Message execute(ActionContext context, C1663_PetPvpInfoRefreshReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Result result = GameContext.getPetApp().refreshPvpInfoList(role);
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
