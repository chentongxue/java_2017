package com.game.draco.app.richman.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2657_RichManRoleUseCardReqMessage;
import com.game.draco.message.response.C2657_RichManRoleUseCardRespMessage;

public class RichManRoleUseCardAction extends BaseAction<C2657_RichManRoleUseCardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2657_RichManRoleUseCardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Result result = GameContext.getRichManApp().roleUseCard(role, reqMsg.getGoodsId(),
				reqMsg.getTargetIds());
		
		if(result.isIgnore()) {
			//快速购买
			return null;
		}
		C2657_RichManRoleUseCardRespMessage respMsg = new C2657_RichManRoleUseCardRespMessage();
		respMsg.setResult(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg;
	}

}
