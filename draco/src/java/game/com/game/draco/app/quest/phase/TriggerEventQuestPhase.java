package com.game.draco.app.quest.phase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.TriggerEventTerm;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.map.point.QuestCollectPoint;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

/**机关触发阶段**/
public class TriggerEventQuestPhase extends QuestPhaseAdator {
	
	private Set<String> idSet = new HashSet<String>();
	
	public TriggerEventQuestPhase(
			String eventId1, int count1, String mapId1,
			String eventId2, int count2, String mapId2,
			String eventId3, int count3, String mapId3) {
			this.init(eventId1, count1, mapId1);
			this.init(eventId2, count2, mapId2);
			this.init(eventId3, count3, mapId3);
	}
	
	public TriggerEventQuestPhase(
			String eventId1, int count1, String mapId1,
			String eventId2, int count2, String mapId2) {
			this(eventId1, count1, mapId1,
				 eventId2, count2, mapId2,
				 null,0,null);
	}
	
	public TriggerEventQuestPhase(String eventId1, int count1, String mapId1) {
			this(eventId1, count1, mapId1,
				null,0,null,
			    null,0,null);
	}
	
	private void init(String eventId, int count, String mapId){
		if(Util.isEmpty(eventId) || count <= 0){
			return;
		}
		this.idSet.add(eventId);
		this.questTermList.add(new TriggerEventTerm(QuestTermType.TriggerEvent, count, eventId, mapId));
	}
	
	@Override
	public int triggerEvent(RoleInstance role,String eventId) {
		if(!idSet.contains(eventId)){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!eventId.equals(term.getParameter())){
				continue ;
			}
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//告知客户端不能再采集
				QuestCollectPoint.notifyDisappear(role, PointType.QuestCollectPoint, eventId);
				//已经满足数量
				return  0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return 0 ;
	}

	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role, String key) {
		if(Util.isEmpty(key)){
			return null ;
		}
		CollectPoint cp = GameContext.getCollectPointLoader().getDataMap().get(key);
		if(null == cp ){
			return null ;
		}
		return cp.getFall() ;
	}
	
	@Override
	public Point getEventPoint(RoleInstance role) {
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//已经满足数量
				continue ;
			}
			TriggerEventTerm thisTerm = (TriggerEventTerm)term ;
			String mapId = thisTerm.getMapId() ;
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map || null == map.getQuestCollectPointConfig()){
				return null ;
			}
			return map.getQuestCollectPointConfig().getRandomPoint(thisTerm.getEventId());
		}
		return  null ;
	}
}
