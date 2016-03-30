package com.game.draco.app.quest.phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.KillMonsterCollectTerm;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 打怪收集阶段
 * 杀怪时按几率掉落物品，收集物品时计数，放弃任务时要删除任务物品
 * 任务和物品关联大，性能较差
 */
public class KillMonsterCollectQuestPhase extends QuestPhaseAdator {

	private Map<String,Pair> npcFallMap = new HashMap<String,Pair>();
	private Map<Integer,Set<String>> goodsIdNpcId = new HashMap<Integer,Set<String>>();
    /**提交任务或者放弃任务的时候需要删除的物品*/
	 private Map<Integer,Integer> goodsMap = new HashMap<Integer,Integer>();
	 
	public KillMonsterCollectQuestPhase(
			String npcId1, int goodsId1, int goodsNum1,int dropProb1,String mapId1,
			String npcId2, int goodsId2, int goodsNum2,int dropProb2,String mapId2,
			String npcId3, int goodsId3, int goodsNum3,int dropProb3,String mapId3) {
		this.init(npcId1, goodsId1, goodsNum1, dropProb1, mapId1);
		this.init(npcId2, goodsId2, goodsNum2, dropProb2, mapId2);
		this.init(npcId3, goodsId3, goodsNum3, dropProb3, mapId3);
		this.hasGoodsEffect = true ;
	}
	
	public KillMonsterCollectQuestPhase(
			String npcId1, int goodsId1, int goodsNum1,int dropProb1,String mapId1,
			String npcId2, int goodsId2, int goodsNum2,int dropProb2,String mapId2) {
		this(npcId1,goodsId1,goodsNum1,dropProb1,mapId1,
			 npcId2,goodsId2,goodsNum2,dropProb2,mapId2,
			 null,0,0,0,null);
	}
	
	public KillMonsterCollectQuestPhase(
			String npcId1, int goodsId1, int goodsNum1,int dropProb1,String mapId1) {
		this(npcId1,goodsId1,goodsNum1,dropProb1,mapId1,
			 null,0,0,0,null,
			 null,0,0,0,null);
	}
	
	private void init(String npcId, int goodsId, int goodsNum,int dropProb, String mapId){
		if(Util.isEmpty(npcId) || goodsId <= 0 || goodsNum <= 0){
			return;
		}
		this.npcFallMap.put(npcId, new Pair(goodsId,dropProb));
		this.questTermList.add(new KillMonsterCollectTerm(QuestTermType.KillMonsterCollect, 
												goodsNum, 
												mapId, 
												npcId, 
												goodsId));
		this.goodsMap.put(goodsId, goodsNum);
		if(!this.goodsIdNpcId.containsKey(goodsId)){
			Set<String> npcIdSet = new HashSet<String>();
			this.goodsIdNpcId.put(goodsId, npcIdSet);
		}
		this.goodsIdNpcId.get(goodsId).add(npcId);
	}
	
	@Override
	public int getGoods(RoleInstance role, int goodsId,int goodsNum){
		if(!this.goodsIdNpcId.containsKey(goodsId)){
			return 0;
		}
		int index = this.startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!String.valueOf(goodsId).equals(term.getParameter())){
				continue ;
			}
			int nowNum = this.getCurrentNum(role, index);
			int oldNum = nowNum - goodsNum ;
			if(oldNum >= term.getCount()){
				//已经满足数量
				return  0;
			}
			//this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum, term, index);
			return 1;
		}
		return 0 ;
	}
	
	@Override
	public int getCurrentNum(RoleInstance role,int index){
		int start = this.startIndex ;
		for(QuestTerm term : this.questTermList){
			start ++ ;
			if(start == index){
				return role.getRoleBackpack().countByGoodsId(Integer.parseInt(term.getParameter()));
			}
		}
		return 0 ;
	}

     @Override
	public Map<Integer,Integer> submitQuestGoodsMap() {
		return this.goodsMap ;
	}

    @Override
	public Map<Integer,Integer> giveupQuestGoodsMap() {
    	//用户自己去丢弃相关物品
    	return this.goodsMap ;
	}
	
	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role, String npcId) {
		if(!this.npcFallMap.containsKey(npcId)){
			return null ;
		}
		Pair pair = this.npcFallMap.get(npcId);
		int index = this.startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!term.getParameter().equals(String.valueOf(pair.getKey()))){
				continue ;
			}
			// 获得当前阶段数目
			int nowNum = this.getCurrentNum(role, index);
			if (nowNum >= term.getCount()) {
				// 此种已经满足数量
				return null;
			}
			//掉落几率
			int rate = pair.getValue();
			if(!ProbabilityMachine.isProbability(rate * ProbabilityMachine.RATE_MODULUS_HUNDRED_MULTI)){
				return null ;
			}
			List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
			goodsList.add(new GoodsOperateBean(pair.getKey(), 1, BindingType.template));
			return goodsList;
		}
		return null;
	}
	
	private class Pair{
		private int key ;
		private int value ;
		
		public Pair(int key,int value){
			this.key = key ;
			this.value = value ;
		}

		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
	
    @Override
	public void giveUp(RoleInstance role) {

    	Map<Integer,Integer> goodsMap =  giveupQuestGoodsMap();
    	Map<Integer,Integer> delMap = new HashMap<Integer,Integer>();
    	for(Integer goodsId : goodsMap.keySet()){
			GoodsBase goods = GameContext.getGoodsApp().getGoodsBase(goodsId.intValue());
			if(goods == null || !(goods instanceof GoodsTask)){
				continue;
			}
			delMap.put(goodsId, goodsMap.get(goodsId));
    	}
    	if(!Util.isEmpty(goodsMap)){
    		GameContext.getUserGoodsApp().deleteSomeForBagByMap(role, delMap, OutputConsumeType.quest_giveup_consume);
        }
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
			KillMonsterCollectTerm thisTerm = (KillMonsterCollectTerm)term ;
			return this.getPoint(thisTerm.getMapId(), thisTerm.getNpcId());
		}
		return null ;
	}
}
