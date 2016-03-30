package com.game.draco.app.quest.phase;

import java.util.HashMap;
import java.util.Map;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.KillNpcFallTerm;

import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 打怪收集阶段
 * 杀怪时不掉落物品，只按几率计数
 * 优化任务的性能
 */
public class KillNpcFallQuestPhase extends QuestPhaseAdator {
	
	/** NPC掉落信息：KEY=npcId,VALUE=掉落对象（物品名称和掉落几率） */
	private Map<String,Pair> npcFallMap = new HashMap<String,Pair>();
	
	public KillNpcFallQuestPhase(
			String npcId1, String goodsName1, int goodsNum1,int dropProb1,String mapId1,
			String npcId2, String goodsName2, int goodsNum2,int dropProb2,String mapId2,
			String npcId3, String goodsName3, int goodsNum3,int dropProb3,String mapId3) {
		this.init(npcId1, goodsName1, goodsNum1, dropProb1, mapId1);
		this.init(npcId2, goodsName2, goodsNum2, dropProb2, mapId2);
		this.init(npcId3, goodsName3, goodsNum3, dropProb3, mapId3);
		this.hasGoodsEffect = true ;
	}
	
	public KillNpcFallQuestPhase(
			String npcId1, String goodsName1, int goodsNum1,int dropProb1,String mapId1,
			String npcId2, String goodsName2, int goodsNum2,int dropProb2,String mapId2) {
		this(   npcId1,goodsName1,goodsNum1,dropProb1,mapId1,
				npcId2,goodsName2,goodsNum2,dropProb2,mapId2,
				null,null,0,0,null
				);
	}
	
	public KillNpcFallQuestPhase(
			String npcId1, String goodsName1, int goodsNum1,int dropProb1,String mapId1) {
		this(   npcId1,goodsName1,goodsNum1,dropProb1,mapId1,
				null,null,0,0,null,
				null,null,0,0,null);
	}
	
	private void init(String npcId, String goodsName, int goodsNum,int dropProb,String mapId){
		if(Util.isEmpty(npcId) || Util.isEmpty(goodsName) || goodsNum <= 0){
			return;
		}
		this.npcFallMap.put(npcId, new Pair(goodsName,dropProb));
		this.questTermList.add(new KillNpcFallTerm(QuestTermType.KillNpcFall, 
												goodsNum, 
												mapId, 
												npcId, 
												goodsName));
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
			KillNpcFallTerm thisTerm = (KillNpcFallTerm)term ;
			return this.getPoint(thisTerm.getMapId(), thisTerm.getNpcId());
		}
		return null ;
	}
	
	@Override
	public int killNpcFallCount(RoleInstance role, String npcId) {
		if(!this.npcFallMap.containsKey(npcId)){
			return 0;
		}
		Pair pair = this.npcFallMap.get(npcId);
		int index = this.startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!term.getParameter().equals(pair.getKey())){
				continue ;
			}
			// 获得当前阶段数目
			int nowNum = this.getCurrentNum(role, index);
			if (nowNum >= term.getCount()) {
				// 此种已经满足数量
				return 0;
			}
			//掉落几率
			int rate = pair.getValue();
			if(!ProbabilityMachine.isProbability(rate * ProbabilityMachine.RATE_MODULUS_HUNDRED_MULTI)){
				return 0 ;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return 0;
	}
	
	private class Pair{
		private String key ;//物品名称
		private int value ;//掉落几率
		
		public Pair(String key,int value){
			this.key = key ;
			this.value = value ;
		}
		
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
	
}
