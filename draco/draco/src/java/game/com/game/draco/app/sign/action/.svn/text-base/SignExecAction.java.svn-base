package com.game.draco.app.sign.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2405_SignExecReqMessage;
import com.game.draco.message.response.C2405_SignExecRespMessage;

public class SignExecAction extends BaseAction<C2405_SignExecReqMessage>{

	@Override
	public Message execute(ActionContext context, C2405_SignExecReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2405_SignExecRespMessage respMsg = new C2405_SignExecRespMessage();
		Result result = GameContext.getSignApp().sign(role);
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg;
	}

}
