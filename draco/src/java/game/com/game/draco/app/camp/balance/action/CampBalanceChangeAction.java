package com.game.draco.app.camp.balance.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1532_CampBalanceChangeReqMessage;
import com.game.draco.message.response.C1532_CampBalanceChangeRespMessage;

public class CampBalanceChangeAction extends BaseAction<C1532_CampBalanceChangeReqMessage> {

	@Override
	public Message execute(ActionContext context, C1532_CampBalanceChangeReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		byte campId = reqMsg.getCampId();
		
		Result result = GameContext.getCampBalanceApp().changeCamp(role, campId);
		if(result.isIgnore()){
			//快速购买
			return null ;
		}
		
		C1532_CampBalanceChangeRespMessage resp = new C1532_CampBalanceChangeRespMessage();
		resp.setCampId(campId);
		
		if(!result.isSuccess()){
			resp.setInfo(result.getInfo());
			return resp;
		}
		resp.setType((byte) 1);
		resp.setInfo(result.getInfo());
		return resp;
	}

}
