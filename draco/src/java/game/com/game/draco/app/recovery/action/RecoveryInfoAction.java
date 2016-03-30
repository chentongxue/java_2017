package com.game.draco.app.recovery.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1928_RecoveryInfoReqMessage;
/**
 * 点击 "一键追回"面板中的“追回”，弹出的详情
 */
public class RecoveryInfoAction  extends BaseAction<C1928_RecoveryInfoReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1928_RecoveryInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
 		if(role == null){
			return null;
		}
		String id = req.getId();
		return GameContext.getRecoveryApp().openRecoveryInfo(role, id);
	}
}
