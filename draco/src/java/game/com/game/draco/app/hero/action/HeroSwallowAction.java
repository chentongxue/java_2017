package com.game.draco.app.hero.action;

import java.util.List;

import org.python.google.common.primitives.Ints;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.vo.HeroSwallowResult;
import com.game.draco.message.request.C1258_HeroSwallowReqMessage;
import com.game.draco.message.response.C1258_HeroSwallowRespMessage;

public class HeroSwallowAction extends BaseAction<C1258_HeroSwallowReqMessage>{

	@Override
	public Message execute(ActionContext context, C1258_HeroSwallowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1258_HeroSwallowRespMessage respMsg = new C1258_HeroSwallowRespMessage();
		int heroId = reqMsg.getHeroId() ;
		HeroSwallowResult result = GameContext.getHeroApp().heroSwallow(role, heroId,
				reqMsg.getSwallowList()) ;
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(result.getInfo());
			return respMsg ;
		}
		List<Integer> swallowHeroList = result.getSwallowHeroList() ;
		if(!Util.isEmpty(swallowHeroList)){
			respMsg.setSwallowHeroIds(Ints.toArray(swallowHeroList));
		}
		respMsg.setHeroInfo(GameContext.getHeroApp().getHeroInfoItem(result.getRoleHero()));
		respMsg.setStatus(result.getStatus());
		return respMsg;
	}

}
