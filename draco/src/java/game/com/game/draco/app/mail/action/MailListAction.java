package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1001_MailListReqMessage;
import com.game.draco.message.response.C1001_MailListRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailListAction extends BaseAction<C1001_MailListReqMessage>{

	@Override
	public Message execute(ActionContext context, C1001_MailListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if (role == null) {
			return null;
		}
		try {
			Status status = GameContext.getMailApp().getMailList(role,
					req.getCurrentPage(), req.getCurrentPageNum());
			if (!status.isSuccess()) {
				C1001_MailListRespMessage resp = new C1001_MailListRespMessage();
				resp.setType(Status.FAILURE.getInnerCode());
				resp.setInfo(status.getTips());
				return resp;
			}
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
}
