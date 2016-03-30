package com.game.draco.app.camp.war.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0351_CampWarApplyCancelReqMessage;
import com.game.draco.message.response.C0351_CampWarApplyCancelRespMessage;

public class CampWarApplyCancelAction extends BaseAction<C0351_CampWarApplyCancelReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C0351_CampWarApplyCancelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context) ;
		if(null == role){
			return null ;
		}
		Result result = GameContext.getCampWarApp().cancel(role);
		C0351_CampWarApplyCancelRespMessage respMsg = new C0351_CampWarApplyCancelRespMessage();
		respMsg.setInfo(result.getInfo());
		respMsg.setStatus(result.getResult());
		return respMsg;
	}

}
