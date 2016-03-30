package com.game.draco.app.quest.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.message.item.QuestTrackItem;
import com.game.draco.message.request.C0708_QuestTrackPanelReqMessage;
import com.game.draco.message.response.C0708_QuestTrackPanelRespMessage;

public class QuestTrackPanelAction extends BaseAction<C0708_QuestTrackPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0708_QuestTrackPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		List<QuestTrackItem> questTrackList = new ArrayList<QuestTrackItem>();
		for(RoleQuestLogInfo questLogInfo : role.getQuestLogMap().values()){
			if(null == questLogInfo){
				continue;
			}
			Quest quest = GameContext.getQuestApp().getQuest(questLogInfo.getQuestId());
			if(null == quest){
				continue;
			}
			QuestTrackItem item = QuestHelper.getQuestTrackInfo(role, quest);
			if(null == item){
				continue;
			}
			questTrackList.add(item);
		}
		QuestTrackItem nextItem = this.getNextQuestTrackItem(role);
		if(null != nextItem){
			questTrackList.add(nextItem);
		}
		C0708_QuestTrackPanelRespMessage resp = new C0708_QuestTrackPanelRespMessage();
		resp.setQuestTrackList(questTrackList);
		return resp;
	}
	
	private QuestTrackItem getNextQuestTrackItem(RoleInstance role){
		int lastFinishQuestId = role.getLastFinishQuestId();
		int nextQuestId = 0;
		//如果是没有做过主线任务，则发第一个主线任务的信息。否则，发下一个主线任务的信息。
		if(lastFinishQuestId <= 0){
			nextQuestId = GameContext.getQuestServiceApp().getFirstMainQuestId();
		} else {
			Quest quest = GameContext.getQuestApp().getQuest(lastFinishQuestId);
			if(null == quest){
				return null;
			}
			nextQuestId = quest.getNextQuestId();
		}
		if(nextQuestId <= 0){
			return null;
		}
		if(role.hasReceiveQuestNow(nextQuestId)){
			return null;
		}
		Quest nextQuest = GameContext.getQuestApp().getQuest(nextQuestId);
		if(null == nextQuest){
			return null;
		}
		return QuestHelper.getQuestTrackInfo(role, nextQuest);
	}
	
}
