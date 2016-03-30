package com.game.draco.app.goddess.action;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.vo.GoddessEnlistResult;
import com.game.draco.message.request.C1362_GoddessEnlistReqMessage;
import com.game.draco.message.response.C1362_GoddessEnlistRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class GoddessEnlistAction extends BaseAction<C1362_GoddessEnlistReqMessage> {

	@Override
	public Message execute(ActionContext context, C1362_GoddessEnlistReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		int goddessId = reqMsg.getGoddessId();
		C1362_GoddessEnlistRespMessage respMsg = new C1362_GoddessEnlistRespMessage();
		GoddessEnlistResult result = GameContext.getGoddessApp().goddessEnlist(role, goddessId);
		respMsg.setGoddessId(goddessId);
		if(!result.isSuccess()) {
			respMsg.setResult(Result.FAIL);
			respMsg.setInfo(result.getInfo());
			return respMsg;
		}
		respMsg.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_enlist_tip,
				result.getGoddessTemplate().getName()));
		respMsg.setResult(Result.SUCCESS);
		return respMsg;
	}

}
