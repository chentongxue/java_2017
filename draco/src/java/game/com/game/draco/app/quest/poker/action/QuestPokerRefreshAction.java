package com.game.draco.app.quest.poker.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0713_QuestPokerRefreshReqMessage;
import com.game.draco.message.response.C0713_QuestPokerRefreshRespMessage;

public class QuestPokerRefreshAction extends BaseAction<C0713_QuestPokerRefreshReqMessage>{

	@Override
	public Message execute(ActionContext context, C0713_QuestPokerRefreshReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getQuestPokerApp().refresh(role);
		if(result.isIgnore()){
			return null;
		}
		C0713_QuestPokerRefreshRespMessage respMsg = new C0713_QuestPokerRefreshRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg ;
	}
	
}
