package com.game.draco.app.mail.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1008_MailTruePickReqMessage;
import com.game.draco.message.response.C1003_MailAccessoryRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MailTrueAccessoryAction extends BaseAction<C1008_MailTruePickReqMessage>{

	@Override
	public Message execute(ActionContext context, C1008_MailTruePickReqMessage req) {
		C1003_MailAccessoryRespMessage resp = new C1003_MailAccessoryRespMessage();
		resp.setType(Status.FAILURE.getInnerCode());
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			String params = req.getParams();
			String mailId = params.split(Cat.comma)[1];
			resp.setMailId(mailId);
			Result result = GameContext.getMailApp().pickTrueMailAccessory(role,params);
			if(result.isIgnore()){
				return null;
			}
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			resp.setType(Status.SUCCESS.getInnerCode());
			resp.setInfo(Status.Mail_Goods_Success.getTips());
		}catch(Exception e){
			logger.error("MailTrueAccessoryAction",e);
		}
		return resp;
	}
}
