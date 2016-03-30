package com.game.draco.app.drama.action;

import com.game.draco.GameContext;
import com.game.draco.app.drama.config.DramaTriggerType;
import com.game.draco.message.request.C3275_DramaInfoReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class DramaInfoAction extends BaseAction<C3275_DramaInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C3275_DramaInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GameContext.getDramaApp().triggerDrama(role, DramaTriggerType.Point,
				reqMsg.getId(), null, 0, null);
		return null;
	}

}
