package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.request.C1268_HeroQualityInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class HeroQualityInfoAction extends BaseAction<C1268_HeroQualityInfoReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C1268_HeroQualityInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int heroId = reqMsg.getHeroId();
		RoleHero hero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
		if (null == hero) {
			// 提示参数错误
			C0002_ErrorRespMessage errMsg = new C0002_ErrorRespMessage();
			errMsg.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return errMsg;
		}
		return GameContext.getHeroApp().buildHeroQualityInfoMessage(hero);
	}

}
