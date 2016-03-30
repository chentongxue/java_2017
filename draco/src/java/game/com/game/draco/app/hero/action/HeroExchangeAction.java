package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1262_HeroExchangeReqMessage;
import com.game.draco.message.response.C1262_HeroExchangeRespMessage;

public class HeroExchangeAction extends BaseAction<C1262_HeroExchangeReqMessage>{

	@Override
	public Message execute(ActionContext context, C1262_HeroExchangeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getHeroApp().heroExchange(role, reqMsg.getHeroId());
		C1262_HeroExchangeRespMessage respMsg = new C1262_HeroExchangeRespMessage();
		respMsg.setInfo(result.getInfo());
		if(result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.SUCCESS);
		}
		respMsg.setHeroId(reqMsg.getHeroId());
		return respMsg;
	}

}
