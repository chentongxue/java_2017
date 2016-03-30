package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2754_UnionActivityListReqMessage;

/**
 * 公会活动列表
 * @author zhb
 *
 */
public class UnionActivityListAction extends BaseAction<C2754_UnionActivityListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2754_UnionActivityListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		GameContext.getUnionApp().validActivity();
		return  GameContext.getUnionApp().sendActivityList(role.getUnionId());
	}

}
