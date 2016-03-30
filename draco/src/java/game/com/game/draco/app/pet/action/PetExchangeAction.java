package com.game.draco.app.pet.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1658_PetExchangeReqMessage;
import com.game.draco.message.response.C1658_PetExchangeRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class PetExchangeAction extends BaseAction<C1658_PetExchangeReqMessage> {

	@Override
	public Message execute(ActionContext context, C1658_PetExchangeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getPetApp().petExchange(role, reqMsg.getPetId());
		C1658_PetExchangeRespMessage resp = new C1658_PetExchangeRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		resp.setPetId(reqMsg.getPetId());
		return resp;
	}

}
