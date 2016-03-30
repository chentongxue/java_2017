package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2755_UnionBossListReqMessage;

/**
 * 公会活动列表
 * @author zhb
 *
 */
public class UnionActivityBossListAction extends BaseAction<C2755_UnionBossListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2755_UnionBossListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return  GameContext.getUnionApp().sendBossDpsList(role.getUnionId(),reqMsg.getActivityId());
	}

}
