package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.MarkingType;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.request.C1253_HeroMarkingReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1253_HeroMarkingRespMessage;

public class HeroMarkingAction extends BaseAction<C1253_HeroMarkingReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C1253_HeroMarkingReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		
		int heroId = reqMsg.getHeroId() ;
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if(null == hero){
			//提示参数错误
			C0002_ErrorRespMessage errMsg = new C0002_ErrorRespMessage();
			errMsg.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return errMsg ;
		}
		//获得配置
		C1253_HeroMarkingRespMessage respMsg = new C1253_HeroMarkingRespMessage();
		respMsg.setHeroId(heroId);
		respMsg.setValor(GameContext.getHeroApp().buildHeroMarkingItem(hero, MarkingType.valor.getType()));
		respMsg.setJustice(GameContext.getHeroApp().buildHeroMarkingItem(hero, MarkingType.justice.getType()));
		return respMsg;
	}

}
