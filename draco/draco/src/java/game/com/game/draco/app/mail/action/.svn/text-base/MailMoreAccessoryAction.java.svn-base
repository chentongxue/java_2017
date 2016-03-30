package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1004_MailMoreAccessoryReqMessage;
import com.game.draco.message.response.C1004_MailMoreAccessoryRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailMoreAccessoryAction extends BaseAction<C1004_MailMoreAccessoryReqMessage>{

	@Override
	public Message execute(ActionContext context, C1004_MailMoreAccessoryReqMessage req) {
		C1004_MailMoreAccessoryRespMessage resp = new C1004_MailMoreAccessoryRespMessage();
		resp.setType(Status.FAILURE.getInnerCode());
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			Result result = GameContext.getMailApp().pickMoreMailAccessory(role, req.getMailIds());
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			return null;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
}
