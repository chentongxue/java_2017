package com.game.draco.app.forward.logic;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class TextTipsLogic implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		if(Util.isEmpty(config.getParameter())){
			return ;
		}
		C0002_ErrorRespMessage msg = new C0002_ErrorRespMessage();
		msg.setInfo(config.getParameter());
		role.getBehavior().sendMessage(msg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.text_tips ;
	}

}
