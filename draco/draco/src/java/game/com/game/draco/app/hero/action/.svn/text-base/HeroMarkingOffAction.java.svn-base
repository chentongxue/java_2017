package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.request.C1260_HeroMarkingOffReqMessage;
import com.game.draco.message.response.C1260_HeroMarkingOffRespMessage;

public class HeroMarkingOffAction extends BaseAction<C1260_HeroMarkingOffReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1260_HeroMarkingOffReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int heroId = reqMsg.getHeroId() ;
		byte markingType = reqMsg.getMarkingType() ;
		C1260_HeroMarkingOffRespMessage respMsg = new C1260_HeroMarkingOffRespMessage();
		Result result = GameContext.getHeroApp().markingOff(role, heroId, markingType) ;
		if(!result.isSuccess()){
			respMsg.setInfo(result.getInfo());
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		respMsg.setHeroId(reqMsg.getHeroId());
		respMsg.setMarkingType(markingType);
		//获得战斗力
		respMsg.setBattleScore(GameContext.getHeroApp().getBattleScore(hero));
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg ;
	}

}
