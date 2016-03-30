package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1354_GoddessLingerInfoReqMessage;

public class GoddessLingerInfoAction extends BaseAction<C1354_GoddessLingerInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C1354_GoddessLingerInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		int goddessId = reqMsg.getId();
		RoleGoddess roleGoddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		if(null == roleGoddess) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Goddess_had_no));
		}
		Message respMsg = GameContext.getGoddessApp().createGoddessLingerInfoMsg(roleGoddess); 
		return respMsg;
	}

}
