package com.game.draco.app.quest.poker.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0712_QuestPokerPanelReqMessage;

public class QuestPokerPanelAction extends BaseAction<C0712_QuestPokerPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0712_QuestPokerPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		return GameContext.getQuestPokerApp().getQuestPokerPanelMessage(role,false);
	}
	
}
