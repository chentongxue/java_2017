package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;

public class ShopSecretLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		C1618_ShopSecretOpenPanelReqMessage reqMsg = new C1618_ShopSecretOpenPanelReqMessage();
		reqMsg.setShopId(config.getParameter());
		role.getBehavior().addEvent(reqMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.shop_secret ;
	}

}
