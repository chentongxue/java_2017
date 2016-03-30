package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C1007_MailNoticeNotifyMessage;
import com.game.draco.message.request.C1007_MailHaveUnreadReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailHaveUnreadAction extends BaseAction<C1007_MailHaveUnreadReqMessage>{

	@Override
	public Message execute(ActionContext context, C1007_MailHaveUnreadReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		//有未读邮件
		if(GameContext.getMailApp().getUnreadMailNumber(role) > 0){
			return new C1007_MailNoticeNotifyMessage();
		}
		return null;
	}
}
