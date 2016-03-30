package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C1401_ExchangeListReqMessage;

public class ExchangeLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		C1401_ExchangeListReqMessage req = new C1401_ExchangeListReqMessage();
		req.setParam(config.getParameter());
		role.getBehavior().addEvent(req);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.exchange ;
	}

}
