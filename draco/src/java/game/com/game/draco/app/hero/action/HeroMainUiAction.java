package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1271_HeroMainUiReqMessage;

public class HeroMainUiAction extends BaseAction<C1271_HeroMainUiReqMessage>{

	@Override
	public Message execute(ActionContext context, C1271_HeroMainUiReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context) ;
		Message message = GameContext.getHeroApp().getHeroMainUiMessage(role);
		role.getBehavior().sendMessage(message);
		//需要下发1272 否则助威的3英雄不可见
		return GameContext.getHeroApp().getHeroSwitchUiMessage(role);
	}

}
