package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1264_HeroLuckPanelReqMessage;

public class HeroLuckPanelAction extends BaseAction<C1264_HeroLuckPanelReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1264_HeroLuckPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		return GameContext.getHeroApp().buildHeroLuckPanel(role);
	}

}
