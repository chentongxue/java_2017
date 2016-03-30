package com.game.draco.app.quest.action;

import com.game.draco.GameContext;
import com.game.draco.app.quest.base.QuestListReqestType;
import com.game.draco.message.request.C0701_QuestListReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QuestListAction extends BaseAction<C0701_QuestListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0701_QuestListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		QuestListReqestType reqestType = QuestListReqestType.get(reqMsg.getType());
		return GameContext.getUserQuestApp().getQuestListRespMessage(role, reqestType);
	}
	
}
