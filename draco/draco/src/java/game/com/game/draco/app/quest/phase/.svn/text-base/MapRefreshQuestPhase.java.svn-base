package com.game.draco.app.quest.phase;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.MapRefreshTerm;

import sacred.alliance.magic.vo.RoleInstance;

/** 杀怪限制 **/
public class MapRefreshQuestPhase extends QuestPhaseAdator {
	
	private String refreshMapId;//地图ID
	private int refreshIndex;//刷怪波次
	
	@Override
	public int mapRefreshNpc(RoleInstance role, int refreshIndex) {
		if(null == role || this.refreshIndex != refreshIndex || !role.getMapId().equals(this.refreshMapId)){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//满足数量
				return  0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return  0;
	}
	
	public MapRefreshQuestPhase(String refreshMapId, short refreshIndex){
		if(refreshIndex <= 0 ){
			return;
		}
		this.refreshMapId = refreshMapId;
		this.refreshIndex = refreshIndex;
		this.questTermList.add(new MapRefreshTerm(QuestTermType.MapRefreshNpc, 
													1, 
													refreshMapId, 
													refreshIndex));
	}
	
}
