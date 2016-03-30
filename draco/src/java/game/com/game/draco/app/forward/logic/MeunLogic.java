package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C0153_MenuFuncReqMessage;

public class MeunLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		C0153_MenuFuncReqMessage reqMsg = new C0153_MenuFuncReqMessage() ;
		reqMsg.setMenuId(Short.parseShort(config.getParameter()));
		role.getBehavior().addEvent(reqMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.menu;
	}

}
