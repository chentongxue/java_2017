package com.game.draco.app.chat.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1806_ChatShowReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class ChatShowAction extends BaseAction<C1806_ChatShowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1806_ChatShowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String param = reqMsg.getParam();
		if (Util.isEmpty(param)) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		role.getBehavior().addCumulateEvent(GameContext.getChatApp().getChatShowMessage(param));
		return null;
	}

}
