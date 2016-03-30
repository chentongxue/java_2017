package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1006_MailMoreDelReqMessage;
import com.game.draco.message.response.C1006_MailMoreDelRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailMoreDelAction extends BaseAction<C1006_MailMoreDelReqMessage>{

	@Override
	public Message execute(ActionContext context, C1006_MailMoreDelReqMessage req) {
		C1006_MailMoreDelRespMessage resp = new C1006_MailMoreDelRespMessage();
		resp.setType(Status.FAILURE.getInnerCode());
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			Status status = GameContext.getMailApp().delMoreMail(role, req.getMailIds());
			if(!status.isSuccess()){
				resp.setInfo(status.getTips());
				return resp;
			}
			return null;
		}catch(Exception e){
			logger.error("",e);
		}
		return null;
	}
}
