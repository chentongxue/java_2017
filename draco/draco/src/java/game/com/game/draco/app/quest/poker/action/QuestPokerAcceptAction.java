package com.game.draco.app.quest.poker.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0714_QuestPokerAcceptReqMessage;

public class QuestPokerAcceptAction extends BaseAction<C0714_QuestPokerAcceptReqMessage>{

	@Override
	public Message execute(ActionContext context, C0714_QuestPokerAcceptReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getQuestPokerApp().accept(role);
		//刷新成功，刷新面板
		if(result.isSuccess()){
			return GameContext.getQuestPokerApp().getQuestPokerPanelMessage(role);
		}
		return new C0003_TipNotifyMessage(result.getInfo());
	}
	
}
