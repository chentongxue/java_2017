package com.game.draco.app.quest.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.ChangeMapEvent;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestPhase;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0719_QuestTransferReqMessage;

public class QuestTransferAction extends BaseAction<C0719_QuestTransferReqMessage>{

	@Override
	public Message execute(ActionContext context, C0719_QuestTransferReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		Quest quest = GameContext.getQuestApp().getQuest(reqMsg.getQuestId());
		if(null == quest){
			return new C0003_TipNotifyMessage(this.getText(TextId.QUEST_NOT_SUPPORT_TRANSFER));
		}
		//活动地图的任务不支持传送
		if(quest.isInActive()){
			return new C0003_TipNotifyMessage(this.getText(TextId.QUEST_NOT_SUPPORT_TRANSFER));
		}
		Point target = this.getTargetPoint(role, quest);
		if(null == target){
			return new C0003_TipNotifyMessage(this.getText(TextId.QUEST_NOT_SUPPORT_TRANSFER));
		}
		//为了让客户中断当前任务寻路
		Point stopPoint = new Point(target.getMapid(),
				target.getX(),target.getY(),ChangeMapEvent.quest.getEventType());
		
		Result result = GameContext.getWorldMapApp().transfer(role, stopPoint,0);
		if(null == result || result.isSuccess() || result.isIgnore()){
			return null ;
		}
		return new C0003_TipNotifyMessage(result.getInfo());
	}
	
	private Point getTargetPoint(RoleInstance role, Quest quest){
		QuestPhase phase = null ;
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(quest.getQuestId());
		if(null == logInfo){
			//未接取的任务,传送到接任务NPC
			phase = quest.getPhaseList().get(0);
		}else {
			phase = quest.getCurrentPhase(role);
		}
		if(null == phase){
			return null;
		}
		return phase.getEventPoint(role);
	}
}
