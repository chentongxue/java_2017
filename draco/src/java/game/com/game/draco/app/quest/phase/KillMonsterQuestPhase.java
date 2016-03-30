package com.game.draco.app.quest.phase;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.KillMonsterTerm;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

/**杀怪阶段**/
public class KillMonsterQuestPhase extends QuestPhaseAdator {

	/**为了提供判断性能*/
	private Set<String> npcIdSet = new HashSet<String>();
	
	@Override
	public int killMonster(RoleInstance role, String npcId) {
		if(Util.isEmpty(npcId) || null == role){
			return 0;
		}
		if(!npcIdSet.contains(npcId)){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!npcId.equals(term.getParameter())){
				continue ;
			}
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//此种怪已经满足数量
				return  0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return  0 ;
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
			KillMonsterTerm thisTerm = (KillMonsterTerm)term ;
			return this.getPoint(thisTerm.getMapId(), thisTerm.getNpcId());
		}
		return null ;
	}
	
	public KillMonsterQuestPhase(String npcId1,int npcCount1,String mapId1){
		this(npcId1, npcCount1,mapId1,
				null,0,null,
				null,0,null);
	}
	
	public KillMonsterQuestPhase(String npcId1,int npcCount1,String mapId1,
								 String npcId2,int npcCount2,String mapId2){
		this(npcId1, npcCount1,mapId1,
				npcId2,npcCount2,mapId2,
				null,0,null);
	}
	
	public KillMonsterQuestPhase(String npcId1, int npcCount1, String mapId1,
			String npcId2,int npcCount2, String mapId2,
			String npcId3,int npcCount3, String mapId3) {
		this.init(npcId1, npcCount1, mapId1);
		this.init(npcId2, npcCount2, mapId2);
		this.init(npcId3, npcCount3, mapId3);
	}
	
	private void init(String npcId, int npcCount, String mapId){
		if(Util.isEmpty(npcId) || npcCount <= 0){
			return;
		}
		this.npcIdSet.add(npcId);
		this.questTermList.add(new KillMonsterTerm(QuestTermType.KillMonster, 
												npcCount, 
												mapId, 
												npcId));
	}
	
}
