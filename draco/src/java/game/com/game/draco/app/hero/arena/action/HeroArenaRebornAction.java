package com.game.draco.app.hero.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1346_HeroArenaRebornReqMessage;
import com.game.draco.message.response.C1346_HeroArenaRebornRespMessage;

public class HeroArenaRebornAction extends BaseAction<C1346_HeroArenaRebornReqMessage>{

	@Override
	public Message execute(ActionContext context, C1346_HeroArenaRebornReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getHeroArenaApp().heroReborn(role);
		C1346_HeroArenaRebornRespMessage resp = new C1346_HeroArenaRebornRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
