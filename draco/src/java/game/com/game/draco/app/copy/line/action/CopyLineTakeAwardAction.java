package com.game.draco.app.copy.line.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0274_CopyLineTakeAwardReqMessage;
import com.game.draco.message.response.C0274_CopyLineTakeAwardRespMessage;

public class CopyLineTakeAwardAction extends BaseAction<C0274_CopyLineTakeAwardReqMessage>{

	@Override
	public Message execute(ActionContext context, C0274_CopyLineTakeAwardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Result result = GameContext.getCopyLineApp().takeAward(role, reqMsg.getChapterId());
		C0274_CopyLineTakeAwardRespMessage resp = new C0274_CopyLineTakeAwardRespMessage();
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}
