package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.config.HeroLove;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.request.C1266_HeroLoveDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1266_HeroLoveDetailRespMessage;

public class HeroLoveDetailAction extends BaseAction<C1266_HeroLoveDetailReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C1266_HeroLoveDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(
				role.getRoleId(), reqMsg.getHeroId());
		/*if (null == roleHero) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}*/
		byte loveType = reqMsg.getLoveType() ;
		HeroLove heroLove = GameContext.getHeroApp().getHeroLove(roleHero.getHeroId(), loveType);
		if(null == heroLove){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		C1266_HeroLoveDetailRespMessage respMsg = new C1266_HeroLoveDetailRespMessage();
		respMsg.setHeroId(reqMsg.getHeroId());
		respMsg.setLoveType(loveType);
		respMsg.setOpenStatus(GameContext.getHeroApp().getHeroLoveStatus(roleHero, loveType));
		respMsg.setDesc(heroLove.getDesc());
		return respMsg;
	}

}
