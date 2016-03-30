package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2757_UnionRoleDpsListReqMessage;

/**
 * 公会活动列表
 * @author zhb
 *
 */
public class UnionActivityRoleDpsListAction extends BaseAction<C2757_UnionRoleDpsListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2757_UnionRoleDpsListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return  GameContext.getUnionApp().sendRoleDpsList(role.getUnionId(), reqMsg.getActivityId(),reqMsg.getGroupId());
	}

}
