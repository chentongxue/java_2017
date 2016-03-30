package com.game.draco.app.quest.poker.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0717_QuestPokerOpenReqMessage;

public class QuestPokerOpenAction extends BaseAction<C0717_QuestPokerOpenReqMessage>{

	@Override
	public Message execute(ActionContext context, C0717_QuestPokerOpenReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		Result result = GameContext.getQuestPokerApp().openPoker(role, reqMsg.getIndex());
		if(result.isIgnore()){
			return null ;
		}
		if(!result.isSuccess()){
			return new C0003_TipNotifyMessage(result.getInfo());
		}
		//打开任务成功，会刷新面板
		return null;
	}
	
}
