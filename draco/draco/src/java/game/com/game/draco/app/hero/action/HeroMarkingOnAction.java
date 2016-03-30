package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.request.C1259_HeroMarkingOnReqMessage;
import com.game.draco.message.response.C1259_HeroMarkingOnRespMessage;

public class HeroMarkingOnAction extends BaseAction<C1259_HeroMarkingOnReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1259_HeroMarkingOnReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int heroId = reqMsg.getHeroId() ;
		byte markingType = reqMsg.getMarkingType() ;
		C1259_HeroMarkingOnRespMessage respMsg = new C1259_HeroMarkingOnRespMessage();
		Result result = GameContext.getHeroApp().markingOn(role, heroId, markingType) ;
		if(!result.isSuccess()){
			respMsg.setInfo(result.getInfo());
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		respMsg.setHeroId(reqMsg.getHeroId());
		respMsg.setMarkingItem(GameContext.getHeroApp().buildHeroMarkingItem(hero, markingType));
		//获得战斗力
		respMsg.setBattleScore(GameContext.getHeroApp().getBattleScore(hero));
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg ;
	}

}
