package com.game.draco.app.title.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2342_TitleActivateReqMessage;
import com.game.draco.message.response.C2342_TitleActivateRespMessage;

public class TitleActivateAction extends BaseAction<C2342_TitleActivateReqMessage>{

	@Override
	public Message execute(ActionContext context, C2342_TitleActivateReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		try{
			Status status = GameContext.getTitleApp().activateTitle(role, req.getTitleId());
			if(!status.isSuccess()){
				C2342_TitleActivateRespMessage resp = new C2342_TitleActivateRespMessage();
				resp.setType(Status.FAILURE.getInnerCode());
				resp.setInfo(status.getTips());
				return resp ;
			}
		}catch(Exception e){
			logger.error("",e);
			C2342_TitleActivateRespMessage resp = new C2342_TitleActivateRespMessage();
			resp.setType(Status.FAILURE.getInnerCode());
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return resp ;
		}
		return null;
	}
}
