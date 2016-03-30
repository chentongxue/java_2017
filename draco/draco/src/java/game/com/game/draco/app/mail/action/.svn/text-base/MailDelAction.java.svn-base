package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1005_MailDelReqMessage;
import com.game.draco.message.response.C1005_MailDelRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailDelAction extends BaseAction<C1005_MailDelReqMessage>{

	@Override
	public Message execute(ActionContext context, C1005_MailDelReqMessage req) {
		C1005_MailDelRespMessage resp = new C1005_MailDelRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			Status status = GameContext.getMailApp().delMail(role, req.getMailId());
			if(!status.isSuccess()){
				resp.setInfo(status.getTips());
				return resp;
			}
			resp.setType(Status.SUCCESS.getInnerCode());
			return resp;
		}catch(Exception e){
			logger.error("",e);
			resp.setType(Status.FAILURE.getInnerCode());
			resp.setInfo(Status.FAILURE.getTips());
			return resp;
		}
	}
}
