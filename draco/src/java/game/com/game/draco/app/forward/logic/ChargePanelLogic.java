package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C2801_ChargeMoneyListReqMessage;

public class ChargePanelLogic implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		role.getBehavior().addEvent(new C2801_ChargeMoneyListReqMessage());
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.charge_panel ;
	}

}
