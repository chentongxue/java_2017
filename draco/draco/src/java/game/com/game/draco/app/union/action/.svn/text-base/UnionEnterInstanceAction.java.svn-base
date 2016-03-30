package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2758_UnionEnterInstanceReqMessage;
import com.game.draco.message.response.C2758_UnionEnterInstanceRespMessage;

/**
 * 进入公会活动
 * @author zhb
 *
 */
public class UnionEnterInstanceAction extends BaseAction<C2758_UnionEnterInstanceReqMessage> {

	@Override
	public Message execute(ActionContext context, C2758_UnionEnterInstanceReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		byte activityId = reqMsg.getActivityId();
		C2758_UnionEnterInstanceRespMessage respMsg = new C2758_UnionEnterInstanceRespMessage();
		Result result = GameContext.getUnionInstanceApp().enterInstance(role, activityId);
		respMsg.setInfo(result.getInfo());
		respMsg.setType(result.getResult());
		return respMsg;
	}

}
