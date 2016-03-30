package com.game.draco.app.goddess.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.vo.GoddessLingerResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1356_GoddessLingerReqMessage;

public class GoddessLingerAction extends BaseAction<C1356_GoddessLingerReqMessage> {

	@Override
	public Message execute(ActionContext context, C1356_GoddessLingerReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		GoddessLingerResult result = GameContext.getGoddessApp().goddessLinger(role, reqMsg.getId());
		if(!result.isSuccess()) {
			C0003_TipNotifyMessage tipsMsg = new C0003_TipNotifyMessage();
			tipsMsg.setMsgContext(result.getInfo());
			return tipsMsg;
		}
		
		return GameContext.getGoddessApp().createGoddessLingerInfoMsg(result.getRoleGoddess());
	}

}
