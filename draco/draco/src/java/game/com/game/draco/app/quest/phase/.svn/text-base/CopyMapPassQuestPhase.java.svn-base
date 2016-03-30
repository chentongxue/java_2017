package com.game.draco.app.quest.phase;

import java.util.HashSet;
import java.util.Set;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.MapTerm;

public class CopyMapPassQuestPhase extends QuestPhaseAdator {

	/** 为了提供判断性能 */
	private Set<String> mapIdSet = new HashSet<String>();
	
	@Override
	public int copyMapPass(RoleInstance role, String mapId) {
		if(null == role || !this.mapIdSet.contains(mapId)){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!mapId.equals(term.getParameter())){
				continue;
			}
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//已经满足数量
				return 0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return 0;
	}
	
	public CopyMapPassQuestPhase(String mapId1, String mapId2, String mapId3) {
		this.init(mapId1);
		this.init(mapId2);
		this.init(mapId3);
	}
	
	public CopyMapPassQuestPhase(String mapId1, String mapId2) {
		this(mapId1, mapId2, null);
	}
	
	public CopyMapPassQuestPhase(String mapId1) {
		this(mapId1, null, null);
	}
	
	private void init(String mapId){
		if(Util.isEmpty(mapId)){
			return;
		}
		String info = "quest script error: questTermType=" + QuestTermType.CopyMapPass + ",mapId=" + mapId + ".";
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			this.phaseCheckFail(info + "it's not exist!");
		}
		if(MapLogicType.copyLogic != map.getMapConfig().getMapLogicType()){
			this.phaseCheckFail(info + "it's not copy map!");
		}
		this.mapIdSet.add(mapId);
		this.questTermList.add(new MapTerm(QuestTermType.CopyMapPass, 1, mapId));
	}
	
}
