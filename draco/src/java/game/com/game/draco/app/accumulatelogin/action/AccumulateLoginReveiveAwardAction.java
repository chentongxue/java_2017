package com.game.draco.app.accumulatelogin.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2521_AccumulateLoginAwardReceiveReqMessage;
/**
 * 
 */
public class AccumulateLoginReveiveAwardAction  extends BaseAction<C2521_AccumulateLoginAwardReceiveReqMessage>{

	@Override
	public Message execute(ActionContext context, C2521_AccumulateLoginAwardReceiveReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte day = reqMsg.getDay();//预留参数
		return GameContext.getAccumulateLoginApp().receiveAccumulateLoginAwards(role,day);
	}
}
