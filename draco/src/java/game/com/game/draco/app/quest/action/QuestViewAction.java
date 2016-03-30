package com.game.draco.app.quest.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.message.item.QuestInfoItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0702_QuestViewReqMessage;
import com.game.draco.message.response.C0702_QuestViewRespMessage;

public class QuestViewAction extends BaseAction<C0702_QuestViewReqMessage>{

	@Override
	public Message execute(ActionContext context, C0702_QuestViewReqMessage reqMsg) {
		int questId = reqMsg.getQuestId();
		if(0 >= questId){
			return new C0003_TipNotifyMessage(Status.Quest_illegality_Id.getTips());
		}
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if(null == quest){
			return new C0003_TipNotifyMessage(Status.Quest_Not_Exist.getTips());
		}
		RoleInstance role = this.getCurrentRole(context);
		RoleQuestLogInfo logInfo = role.getQuestLogMap().get(questId);
		QuestStatus status = QuestStatus.noneTask;
		if(null != logInfo){
			status = QuestHelper.getQuestStatus(role, quest, logInfo);
		}else if(quest.canAccept(role)){
			status = QuestStatus.canAccept;
		}
		C0702_QuestViewRespMessage respMsg = new C0702_QuestViewRespMessage();
		respMsg.setStatus((byte) status.getType()); 
		QuestInfoItem questInfoItem = QuestHelper.getQuestInfoItem(role, quest,QuestHelper.Where.View.getType());
		/*//未接任务不显示任务描述和任务条件
		if(QuestStatus.canAccept == status || QuestStatus.noneTask == status){
			questInfoItem.setDesc("");
			questInfoItem.setTermItems(new ArrayList<QuestTermItem>());
		}*/
		if(quest.getTimeLimit() <=0){
			respMsg.setQuestInfoItem(questInfoItem);
			return respMsg;
		}
		// 限时任务需要显示剩余时间
		questInfoItem.setDesc(questInfoItem.getDesc() + QuestHelper.getTimeLimitQuestTimeInfo(role,quest,true));
		respMsg.setQuestInfoItem(questInfoItem);
		return respMsg;
	}
}
