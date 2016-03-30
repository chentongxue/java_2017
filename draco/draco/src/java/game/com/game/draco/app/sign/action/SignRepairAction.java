package com.game.draco.app.sign.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2406_SignRepairReqMessage;
import com.game.draco.message.response.C2406_SignRepairRespMessage;

public class SignRepairAction extends BaseAction<C2406_SignRepairReqMessage> {

	@Override
	public Message execute(ActionContext context, C2406_SignRepairReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2406_SignRepairRespMessage respMsg = new C2406_SignRepairRespMessage();
		Result result = GameContext.getSignApp().signRepair(role);
		respMsg.setInfo(result.getInfo());
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			return respMsg ;
		}
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg;
	}

}
