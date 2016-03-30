package com.game.draco.app.quest.poker.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0713_QuestPokerRefreshReqMessage;

public class QuestPokerRefreshAction extends BaseAction<C0713_QuestPokerRefreshReqMessage>{

	@Override
	public Message execute(ActionContext context, C0713_QuestPokerRefreshReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getQuestPokerApp().refresh(role);
		//刷新失败，提示信息
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		return null;
	}
	
}
