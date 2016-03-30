package com.game.draco.app.hero.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1342_HeroArenaFightReqMessage;
import com.game.draco.message.response.C1342_HeroArenaFightRespMessage;

public class HeroArenaFightAction extends BaseAction<C1342_HeroArenaFightReqMessage>{

	@Override
	public Message execute(ActionContext context, C1342_HeroArenaFightReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		
		Result result = GameContext.getHeroArenaApp().fighting(role);
		if(!result.isSuccess()){
			C1342_HeroArenaFightRespMessage resp = new C1342_HeroArenaFightRespMessage();
			resp.setType(result.getResult());
			resp.setInfo(result.getInfo());
			return resp;
		}
		return null;
	}

}
