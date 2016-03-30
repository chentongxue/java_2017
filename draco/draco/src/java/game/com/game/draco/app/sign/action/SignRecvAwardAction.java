package com.game.draco.app.sign.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2407_SignRecvAwardReqMessage;
import com.game.draco.message.response.C2407_SignRecvAwardRespMessage;

public class SignRecvAwardAction extends BaseAction<C2407_SignRecvAwardReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C2407_SignRecvAwardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte times = reqMsg.getTimes() ;
		C2407_SignRecvAwardRespMessage respMsg = new C2407_SignRecvAwardRespMessage();
		Result result = GameContext.getSignApp().recvAward(role, times);
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		respMsg.setTimes(times);
		return respMsg;
	}

}
