package com.game.draco.app.equip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1276_HeroEquipStarDetailReqMessage;

public class HeroEquipStarDetailAction extends BaseAction<C1276_HeroEquipStarDetailReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C1276_HeroEquipStarDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		return GameContext.getEquipApp().getHeroEquipStarDetailRespMessage(role,
				reqMsg.getHeroId(), reqMsg.getGoodsInstanceId(), reqMsg.getPos());
	}

}
