package com.game.draco.app.quest.poker.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0715_QuestPokerSubmitReqMessage;

public class QuestPokerSubmitAction extends BaseAction<C0715_QuestPokerSubmitReqMessage>{

	@Override
	public Message execute(ActionContext context, C0715_QuestPokerSubmitReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Result result = GameContext.getQuestPokerApp().submit(role);
		//刷新成功，刷新面板
		if(result.isSuccess()){
			return GameContext.getQuestPokerApp().getQuestPokerPanelMessage(role,false);
		}
		//失败返回提示信息
		return new C0003_TipNotifyMessage(result.getInfo());
	}
	
}
