package com.game.draco.app.recovery.action;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1925_RecoveryPanelReqMessage;
/**
 * 打开 "一键追回"面板 
 */
public class RecoveryPanelAction  extends BaseAction<C1925_RecoveryPanelReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1925_RecoveryPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		return GameContext.getRecoveryApp().openRecoveryPanel(role);
	}
}
