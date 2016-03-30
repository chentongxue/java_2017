package com.game.draco.app.camp.war.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0350_CampWarApplyReqMessage;
import com.game.draco.message.response.C0350_CampWarApplyRespMessage;

public class CampWarApplyAction extends BaseAction<C0350_CampWarApplyReqMessage>{

	@Override
	public Message execute(ActionContext context, C0350_CampWarApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context) ;
		if(null == role){
			return null ;
		}
		Result result = GameContext.getCampWarApp().apply(role);
		C0350_CampWarApplyRespMessage respMsg = new C0350_CampWarApplyRespMessage();
		respMsg.setInfo(result.getInfo());
		respMsg.setStatus(result.getResult());
		return respMsg;
	}

}
