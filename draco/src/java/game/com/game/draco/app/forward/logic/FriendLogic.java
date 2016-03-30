package com.game.draco.app.forward.logic;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.request.C1205_SocialFriendListReqMessage;

public class FriendLogic implements ForwardLogic {

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		C1205_SocialFriendListReqMessage reqMsg = new C1205_SocialFriendListReqMessage();
		role.getBehavior().addEvent(reqMsg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.friend ;
	}

}
