package com.game.draco.app.quest.poker.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0720_QuestPokerRefreshQuestReqMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QuestPokerRefreshQuestAction extends BaseAction<C0720_QuestPokerRefreshQuestReqMessage>{

	@Override
	public Message execute(ActionContext context, C0720_QuestPokerRefreshQuestReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getQuestPokerApp().refreshQuest(role);
		if(result.isIgnore() || result.isSuccess()){
			return null;
		}
		C0003_TipNotifyMessage tipsMsg = new C0003_TipNotifyMessage() ;
		tipsMsg.setMsgContext(result.getInfo());
		return tipsMsg ;
	}
	
}
