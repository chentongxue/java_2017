package com.game.draco.app.hero.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1341_HeroArenaSelectReqMessage;
import com.game.draco.message.response.C1341_HeroArenaSelectRespMessage;

public class HeroArenaSelectAction extends BaseAction<C1341_HeroArenaSelectReqMessage>{

	@Override
	public Message execute(ActionContext context, C1341_HeroArenaSelectReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getHeroArenaApp().selectHeros(role, reqMsg.getSelectHeros());
		C1341_HeroArenaSelectRespMessage resp = new C1341_HeroArenaSelectRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
