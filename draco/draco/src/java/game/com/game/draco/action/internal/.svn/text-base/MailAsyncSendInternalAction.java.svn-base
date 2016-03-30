package com.game.draco.action.internal;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.message.internal.C0084_MailAsyncSendInternalMessage;

public class MailAsyncSendInternalAction extends BaseAction<C0084_MailAsyncSendInternalMessage> {

	@Override
	public Message execute(ActionContext context, C0084_MailAsyncSendInternalMessage reqMsg) {
		try {
			Mail mail = reqMsg.getMail();
			if(null != mail){
				GameContext.getMailApp().sendMail(mail);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
		}
		return null ;
	}

}
