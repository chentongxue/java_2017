package com.game.draco.app.quest.action;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.message.request.C0703_QuestBeforeOperateReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QuestBeforeOperateAction extends BaseAction<C0703_QuestBeforeOperateReqMessage>{

	@Override
	public Message execute(ActionContext context, C0703_QuestBeforeOperateReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		String param = reqMsg.getParam();
		return QuestHelper.questBeforeOperateRespMessageBuilder(role, param);
	}
	
}
