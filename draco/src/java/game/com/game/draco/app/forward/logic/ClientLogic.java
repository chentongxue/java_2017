package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.response.C1123_ForwardRespMessage;

public class ClientLogic implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		C1123_ForwardRespMessage respMsg = new C1123_ForwardRespMessage();
		respMsg.setType(Byte.parseByte(config.getParameter()));
		role.getBehavior().sendMessage(respMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.client_logic ;
	}

}
