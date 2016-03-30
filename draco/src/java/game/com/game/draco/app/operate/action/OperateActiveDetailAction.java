package com.game.draco.app.operate.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2452_OperateActiveDetailReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class OperateActiveDetailAction extends BaseAction<C2452_OperateActiveDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C2452_OperateActiveDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		return GameContext.getOperateActiveApp().getOperateActiveDetail(role, reqMsg.getActiveId());
	}

}
