package com.game.draco.app.quest.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0706_QuestGiveupReqMessage;

public class QuestGiveupAction extends BaseAction<C0706_QuestGiveupReqMessage>{

	@Override
	public Message execute(ActionContext context, C0706_QuestGiveupReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int questId = reqMsg.getQuestId();
		if(questId <= 0){
			return new C0003_TipNotifyMessage(this.getText(TextId.Quest_illegality_Id));
		}
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if(null == quest){
			return new C0003_TipNotifyMessage(this.getText(TextId.Quest_Not_Exist));
		}
		//不可放弃的任务，提示玩家不能放弃；炸金花任务不能放弃
		if(!quest.isCanGiveUp() || QuestAcceptType.Poker == quest.getQuestAcceptType()){
			return new C0003_TipNotifyMessage(this.getText(TextId.Quest_Not_Giveup));
		}
		try {
			//返回消息在放弃逻辑里构建发送
			GameContext.getUserQuestApp().giveUpQuest(role, questId);
			return null;
		} catch (ServiceException e) {
			this.logger.error(this.getClass().getName() + " quest giveup error: ", e);
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
	}
	
}
