package com.game.draco.app.forward.logic;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C1604_NpcStoreAnytimeReqMessage;
import sacred.alliance.magic.vo.RoleInstance;

public class ShopAnytimeLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		C1604_NpcStoreAnytimeReqMessage reqMsg = new C1604_NpcStoreAnytimeReqMessage();
		role.getBehavior().addEvent(reqMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.shop_anytime ;
	}

}
