package com.game.draco.app.qualify.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1757_QualifyRulesDescReqMessage;
import com.game.draco.message.response.C1757_QualifyRulesDescRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QualifyRankDesc extends BaseAction<C1757_QualifyRulesDescReqMessage> {

	@Override
	public Message execute(ActionContext context, C1757_QualifyRulesDescReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C1757_QualifyRulesDescRespMessage resp = new C1757_QualifyRulesDescRespMessage();
		resp.setDesc(GameContext.getQualifyApp().getQualifyRankDesc());
		return resp;
	}

}
