package com.game.draco.app.camp.balance.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1535_CampBalanceSelectReqMessage;
import com.game.draco.message.response.C1535_CampBalanceSelectRespMessage;

public class CampBalanceSelectAction extends BaseAction<C1535_CampBalanceSelectReqMessage> {

	@Override
	public Message execute(ActionContext context, C1535_CampBalanceSelectReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		byte campId = reqMsg.getCampId();
		C1535_CampBalanceSelectRespMessage resp = new C1535_CampBalanceSelectRespMessage();
		Result result = GameContext.getCampBalanceApp().selectCamp(role, campId);
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
