package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C1651_PetListReqMessage;

public class PetPlunderLogic  implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		role.getBehavior().addEvent(new C1651_PetListReqMessage());
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.pet_plunder ;
	}

}
