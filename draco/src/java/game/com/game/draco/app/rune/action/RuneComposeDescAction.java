package com.game.draco.app.rune.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0546_RuneComposeDescReqMessage;
import com.game.draco.message.response.C0546_RuneComposeDescRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class RuneComposeDescAction extends BaseAction<C0546_RuneComposeDescReqMessage> {

	@Override
	public Message execute(ActionContext context, C0546_RuneComposeDescReqMessage reqMsg) {
		String desc = "";
		if (0 == reqMsg.getType()) {
			// 合成
			desc = GameContext.getRuneApp().getComposeDesc();
		} else {
			// 熔炼
			desc = GameContext.getRuneApp().getSmeltDesc();
		}
		C0546_RuneComposeDescRespMessage resp = new C0546_RuneComposeDescRespMessage();
		resp.setInfo(desc);
		return resp;
	}

}
