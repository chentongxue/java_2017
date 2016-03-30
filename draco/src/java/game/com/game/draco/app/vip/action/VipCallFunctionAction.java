package com.game.draco.app.vip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2512_VipCallFunctionReqMessage;
/**
 * 如果是没有召唤过此功能，则弹出消耗面板
 */
public class VipCallFunctionAction  extends BaseAction<C2512_VipCallFunctionReqMessage>{
	@Override
	public Message execute(ActionContext ct, C2512_VipCallFunctionReqMessage req) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		if(Util.isEmpty(req.getFunctionId())){
			return null;
		}
		String [] param = req.getFunctionId().split(",");
		byte confirm = 0;
		if(param.length>1){
			confirm = 1;
		}
		String functionId = param[0];
		return GameContext.getVipApp().callVipFunction(role, functionId, confirm);
	}
}
