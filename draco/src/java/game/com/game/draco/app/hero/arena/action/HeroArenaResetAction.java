package com.game.draco.app.hero.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1349_HeroArenaResetReqMessage;
import com.game.draco.message.response.C1349_HeroArenaResetRespMessage;

public class HeroArenaResetAction extends BaseAction<C1349_HeroArenaResetReqMessage>{

	@Override
	public Message execute(ActionContext context, C1349_HeroArenaResetReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			Result result = GameContext.getHeroArenaApp().resetHeroArena(role);
			C1349_HeroArenaResetRespMessage respMsg = new C1349_HeroArenaResetRespMessage();
			respMsg.setSuccess(result.getResult());
			respMsg.setInfo(result.getInfo());
			return respMsg;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Error));
		}
	}

}
