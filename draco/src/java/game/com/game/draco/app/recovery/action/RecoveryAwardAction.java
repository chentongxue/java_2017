package com.game.draco.app.recovery.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1926_RecoveryReqMessage;
/**
 * "一键追回"昨天未得到的奖励 
 */
public class RecoveryAwardAction  extends BaseAction<C1926_RecoveryReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1926_RecoveryReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		byte consumeType = req.getConsumeType();
		String id = req.getId();
		return GameContext.getRecoveryApp().recoveryAward(role, id, consumeType);
	}

	
}
