package com.game.draco.app.operate.firstpay.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.firstpay.config.FirstPayBaseConfig;
import com.game.draco.message.request.C2459_FirstPayInfoReqMessage;

public class OperateActiveFirstPayInfoAction extends BaseAction<C2459_FirstPayInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C2459_FirstPayInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		FirstPayBaseConfig baseConfig = GameContext.getFirstPayApp().getFirstPayBaseConfig();
		return GameContext.getOperateActiveApp().getOperateActive(baseConfig.getActiveId()).getOperateActiveDetail(role);
	}

}
