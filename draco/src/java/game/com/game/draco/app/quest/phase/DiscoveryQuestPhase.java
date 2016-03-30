package com.game.draco.app.quest.phase;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.MapTerm;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class DiscoveryQuestPhase extends QuestPhaseAdator {

	/**为了提供判断性能*/
	private Set<String> mapIdSet = new HashSet<String>();
	
	
	public DiscoveryQuestPhase(String mapId1, String mapId2, String mapId3) {
		this.init(mapId1);
		this.init(mapId2);
		this.init(mapId3);
	}
	
	public DiscoveryQuestPhase(String mapId1, String mapId2) {
		this(mapId1, mapId2, null);
	}
	
	public DiscoveryQuestPhase(String mapId1) {
		this(mapId1, null, null);
	}
	
	private void init(String mapId){
		if(!Util.isEmpty(mapId)){
			this.mapIdSet.add(mapId);
			this.questTermList.add(new MapTerm(QuestTermType.Map, 1, mapId));
		}
	}
	
	@Override
	public int enterMap(RoleInstance role) {
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return 0;
		}
		String mapId = mapInstance.getMap().getMapId();
		if(Util.isEmpty(mapId)){
			return 0;
		}
		if(!this.mapIdSet.contains(mapId)){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm current : questTermList){
			index ++ ;
			if(!mapId.equals(current.getParameter())){
				continue ;
			}
			//获得日志
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum > 0){
				//已经来过
				return 0;
			}
			//标识
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, current, index);
			return 1;
		}
		return 0 ;
	}

	@Override
	public boolean isPhaseComplete(RoleInstance role) {
		int size = this.mapIdSet.size();
		//只探索一张地图，判断当前地图是否是目标地图
		if(1 == size){
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return false;
			}
			String mapId = mapInstance.getMap().getMapId();
			if(Util.isEmpty(mapId)){
				return false;
			}
			return this.mapIdSet.contains(mapId);
		}
		return super.isPhaseComplete(role);
	}
	
	
	@Override
	public Point getEventPoint(RoleInstance role) {
		int index = this.startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			// 获得当前阶段数目
			int nowNum = this.getCurrentNum(role, index);
			if (nowNum >= term.getCount()) {
				// 此种已经满足数量
				continue ;
			}
			MapTerm thisTerm = (MapTerm)term ;
			Map map = GameContext.getMapApp().getMap(thisTerm.getMapId());
			if(null == map){
				return null ;
			}
			return new Point(thisTerm.getMapId(),
					map.getMapConfig().getMaporiginx(),
					map.getMapConfig().getMaporiginy());
		}
		return null ;
	}
}
