package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1003_MailAccessoryReqMessage;
import com.game.draco.message.response.C1003_MailAccessoryRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailAccessoryAction extends BaseAction<C1003_MailAccessoryReqMessage>{

	@Override
	public Message execute(ActionContext context, C1003_MailAccessoryReqMessage req) {
		C1003_MailAccessoryRespMessage resp = new C1003_MailAccessoryRespMessage();
		resp.setType(Status.FAILURE.getInnerCode());
		try{
			String mailId = req.getMailId();
			resp.setMailId(mailId);
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			Result result = GameContext.getMailApp().pickMailAccessory(role, mailId, false);
			//付费邮件发了二次确认消息
			if(result.isIgnore()){
				return null;
			}
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType(Status.SUCCESS.getInnerCode());
			return resp;
		}catch(Exception e){
			logger.error("MailAccessoryAction",e);
			return resp;
		}
	}
}
