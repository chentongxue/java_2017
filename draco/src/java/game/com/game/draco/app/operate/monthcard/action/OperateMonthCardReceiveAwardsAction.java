package com.game.draco.app.operate.monthcard.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2457_MonthCardRewardReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2457_MonthCardRewardRespMessage;

public class OperateMonthCardReceiveAwardsAction extends BaseAction<C2457_MonthCardRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2457_MonthCardRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getMonthCardApp().receiveAwards(role);
		if (!result.isSuccess()) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(result.getInfo());
			return message;
		}
		return new C2457_MonthCardRewardRespMessage();
	}
	
}
