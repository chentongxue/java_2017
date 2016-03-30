package com.game.draco.app.quest.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.message.request.C0718_QuestSubmitViewReqMessage;

public class QuestSubmitViewAction extends BaseAction<C0718_QuestSubmitViewReqMessage>{

	@Override
	public Message execute(ActionContext context, C0718_QuestSubmitViewReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int questId = reqMsg.getQuestId();
		if(questId <= 0){
			return null;
		}
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if(null == quest){
			return null;
		}
		String mapId;
		String npcId;
		if(role.hasReceiveQuestNow(questId)){
			mapId = quest.getSubmitMapId();
			npcId = quest.getSubmitNpcId();
		}else{
			mapId = quest.getAcceptMapId();
			npcId = quest.getAcceptNpcId();
		}
		//任务有所在地图，而角色当前不再该地图，则不需要主推任务详情
		if(!Util.isEmpty(mapId) && !role.getMapId().equals(mapId)){
			return null;
		}
		QuestAcceptType acceptType = quest.getQuestAcceptType();
		//如果是在NPC处接取的任务，则主推任务详情
		if(QuestAcceptType.Npc == acceptType){
			GameContext.getUserQuestApp().pushQuestViewMessage(role, npcId);
			return null;
		}
		//如果是扑克任务，则弹开扑克任务面板
		if(QuestAcceptType.Poker == acceptType){
			return GameContext.getQuestPokerApp().getQuestPokerPanelMessage(role,false);
		}
		return null;
	}
	
}
