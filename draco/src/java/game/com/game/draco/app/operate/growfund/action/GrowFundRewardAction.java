package com.game.draco.app.operate.growfund.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2463_GrowFundRewardReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2463_GrowFundRewardRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GrowFundRewardAction extends BaseAction<C2463_GrowFundRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2463_GrowFundRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getGrowFundApp().reward(role, reqMsg.getLevel());
		if (!result.isSuccess()) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(result.getInfo());
			return message;
		}
		C2463_GrowFundRewardRespMessage resp = new C2463_GrowFundRewardRespMessage();
		resp.setLevel(reqMsg.getLevel());
		return resp;
	}

}
