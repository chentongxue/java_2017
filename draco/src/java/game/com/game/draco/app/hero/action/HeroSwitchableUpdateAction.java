package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1269_HeroSwitchableUpdateReqMessage;
import com.game.draco.message.response.C1254_HeroOnBattleRespMessage;

public class HeroSwitchableUpdateAction extends BaseAction<C1269_HeroSwitchableUpdateReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1269_HeroSwitchableUpdateReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		//C1269_HeroSwitchableUpdateRespMessage respMsg = new C1269_HeroSwitchableUpdateRespMessage();
		//需要刷新cd直接用1254
		C1254_HeroOnBattleRespMessage respMsg = new C1254_HeroOnBattleRespMessage();
		Result result = GameContext.getHeroApp().updateSwitchableHero(role, reqMsg.getHeroIds(),reqMsg.getHelpHeroIds());
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		respMsg.setCd(GameContext.getHeroApp().getOnBattleCd(role));
		return respMsg;
	}

}
