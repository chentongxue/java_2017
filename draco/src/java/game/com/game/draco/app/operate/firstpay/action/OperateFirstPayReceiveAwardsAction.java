package com.game.draco.app.operate.firstpay.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2460_FirstPayRewardReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class OperateFirstPayReceiveAwardsAction extends BaseAction<C2460_FirstPayRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2460_FirstPayRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getFirstPayApp().receiveAwards(role);
		if (!result.isSuccess()) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(result.getInfo());
			return message;
		}
		return null;
	}
	
}
