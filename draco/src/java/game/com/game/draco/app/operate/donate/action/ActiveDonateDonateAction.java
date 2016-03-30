package com.game.draco.app.operate.donate.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2393_ActiveDonateDonateReqMessage;

public class ActiveDonateDonateAction extends BaseAction<C2393_ActiveDonateDonateReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C2393_ActiveDonateDonateReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Result result = GameContext.getDonateApp().donate(role,
				reqMsg.getActiveId());
		C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage(
				result.getInfo());
		if (!result.isSuccess()) {
			return tips;
		}
		role.getBehavior().sendMessage(tips);
		// 刷新界面
		return GameContext.getDonateApp().createDonateDetailMsg(role,
				reqMsg.getActiveId());
	}

}
