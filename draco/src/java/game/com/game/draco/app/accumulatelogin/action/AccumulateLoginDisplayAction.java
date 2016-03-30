package com.game.draco.app.accumulatelogin.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2520_AccumulateLoginReqMessage;
/**
 * 
 */
public class AccumulateLoginDisplayAction  extends BaseAction<C2520_AccumulateLoginReqMessage>{

	@Override
	public Message execute(ActionContext context, C2520_AccumulateLoginReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		return GameContext.getAccumulateLoginApp().openAccumulateLoginPanel(role);
	}
}
