package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C2600_RoleHorseListReqMessage;

public class HorsePanelLogic  implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		role.getBehavior().addEvent(new C2600_RoleHorseListReqMessage());
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.horse_panel ;
	}

}
