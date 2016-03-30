package com.game.draco.app.npc.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0613_NpcInspireReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcInspireAction extends BaseAction<C0613_NpcInspireReqMessage>{

	@Override
	public Message execute(ActionContext context, C0613_NpcInspireReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Result result = GameContext.getNpcInspireApp().inspire(role, reqMsg.getParam());
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		return null;
	}
	
}
