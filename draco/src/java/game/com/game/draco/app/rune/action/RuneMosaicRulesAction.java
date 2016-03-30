package com.game.draco.app.rune.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0581_GoodsRuneMosaicRulesReqMessage;
import com.game.draco.message.response.C0581_GoodsRuneMosaicRulesRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RuneMosaicRulesAction extends BaseAction<C0581_GoodsRuneMosaicRulesReqMessage> {

	@Override
	public Message execute(ActionContext context, C0581_GoodsRuneMosaicRulesReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C0581_GoodsRuneMosaicRulesRespMessage resp = new C0581_GoodsRuneMosaicRulesRespMessage();
		byte[] rules = GameContext.getRuneApp().getMoasicRules(reqMsg.getType());
		resp.setIsMutex((byte) 1);
		resp.setRules(rules);
		resp.setType(reqMsg.getType());
		return resp;
	}

}
