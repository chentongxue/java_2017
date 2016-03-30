package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.message.request.C1103_HeroShowReqMessage;

/**
 * 查看别的玩家英雄 
 */
public class HeroShowAction extends BaseAction<C1103_HeroShowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1103_HeroShowReqMessage reqMsg) {
		return null;
	}

}
