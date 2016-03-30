package com.game.draco.app.hero.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1347_HeroArenaPanelReqMessage;

public class HeroArenaPanelAction extends BaseAction<C1347_HeroArenaPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C1347_HeroArenaPanelReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			return GameContext.getHeroArenaApp().getHeroArenaPanelMessage(role);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Error));
		}
	}

}
