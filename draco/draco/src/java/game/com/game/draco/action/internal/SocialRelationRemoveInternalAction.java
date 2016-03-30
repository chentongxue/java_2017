package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0059_SocialRelationRemoveInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class SocialRelationRemoveInternalAction extends BaseAction<C0059_SocialRelationRemoveInternalMessage>{

	@Override
	public Message execute(ActionContext context, C0059_SocialRelationRemoveInternalMessage reqMsg) {
		GameContext.getSocialApp().logoutRemoveRoleSocialRelation(reqMsg.getRoleId());
		return null;
	}

}
