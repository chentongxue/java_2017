package com.game.draco.app.copy.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0209_CopyEnterReqMessage;

/**
 * 进入副本（普通，英雄）
 */
public class CopyEnterAction extends BaseAction<C0209_CopyEnterReqMessage> {

	@Override
	public Message execute(ActionContext context, C0209_CopyEnterReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (role == null) {
			return null;
		}
		Result result = GameContext.getCopyLogicApp().enterCopy(role, reqMsg.getCopyId());
		if (result.isIgnore()) {
			return null;
		}
		if (!result.isSuccess()) {
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(result.getInfo());
			return message;
		}
		return null;
	}

}
