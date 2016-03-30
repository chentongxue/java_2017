package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1254_HeroOnBattleReqMessage;
import com.game.draco.message.response.C1254_HeroOnBattleRespMessage;

public class HeroOnBattleAction extends BaseAction<C1254_HeroOnBattleReqMessage>{

	@Override
	public Message execute(ActionContext context, C1254_HeroOnBattleReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
        if(role.isDeath()){
            //如果角色已经死亡则再次push死亡面板
            role.getBehavior().death(null);
            return null;
        }
		C1254_HeroOnBattleRespMessage respMsg = new C1254_HeroOnBattleRespMessage();
		Result result = GameContext.getHeroApp().onBattle(role, reqMsg.getHeroId());
		respMsg.setInfo(result.getInfo());
		respMsg.setStatus(result.getResult());
		respMsg.setCd(GameContext.getHeroApp().getOnBattleCd(role));
		return respMsg ;
	}

}
