package com.game.draco.app.quest.phase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.GoodsTerm;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsCollectQuestPhase extends QuestPhaseAdator {
	
	/**为了提供判断性能*/
	private Set<Integer> goodsIdSet = new HashSet<Integer>();
    /**提交任务或者放弃任务的时候需要删除的物品*/
    private Map<Integer,Integer> goodsMap = new HashMap<Integer,Integer>();
	
	public GoodsCollectQuestPhase(
			int goodsId1,int goodsNum1,String mapId1,String npcId1,
			int goodsId2,int goodsNum2,String mapId2,String npcId2,
			int goodsId3,int goodsNum3,String mapId3,String npcId3) {
		this.init(goodsId1, goodsNum1,mapId1,npcId1);
		this.init(goodsId2, goodsNum2,mapId2,npcId2);
		this.init(goodsId3, goodsNum3,mapId3,npcId3);
		this.hasGoodsEffect = true ;
	}
	
	public GoodsCollectQuestPhase(
			int goodsId1,int goodsNum1,String mapId1,String npcId1,
			int goodsId2,int goodsNum2,String mapId2,String npcId2) {
		this(goodsId1,goodsNum1,mapId1,npcId1,
			goodsId2,goodsNum2,mapId2,npcId2,
			0,0,null,null);
	}
	
	public GoodsCollectQuestPhase(int goodsId, int goodsNum, String mapId, String npcId) {
		this(goodsId, goodsNum, mapId, npcId,
			0,0,null,null,
			0,0,null,null);
	}
	
	public GoodsCollectQuestPhase(int goodsId, int goodsNum) {
		this(goodsId, goodsNum, null, null,
			0,0,null,null,
			0,0,null,null);
	}
	
	private void init(int goodsId,int goodsNum,String mapId, String npcId){
		if(goodsId <= 0 && goodsNum <= 0){
			return;
		}
		this.goodsIdSet.add(goodsId);
		this.questTermList.add(new GoodsTerm(QuestTermType.Goods, 
												goodsNum, 
												goodsId, 
												mapId, 
												npcId));
		this.goodsMap.put(goodsId, goodsNum);
	}
	
	@Override
	public int getGoods(RoleInstance role, int goodsId,int goodsNum){
		if(!this.goodsIdSet.contains(goodsId)){
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
			int maxNum = term.getCount();
			if(oldNum >= maxNum){
				//已经满足数量
				return  0;
			}
			int currNum = Math.min(nowNum, maxNum);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, currNum, term, index);
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
	public boolean isPhaseComplete(RoleInstance role) {
		for(Map.Entry<Integer, Integer> entry : this.goodsMap.entrySet()){
			if(null == entry){
				continue;
			}
			int goodsNum = entry.getValue();
			int currNum = role.getRoleBackpack().countByGoodsId(entry.getKey());
			if(currNum < goodsNum){
				return false;
			}
		}
		return true;
	}
	
    @Override
	public Map<Integer,Integer> submitQuestGoodsMap() {
		return goodsMap ;
	}

    @Override
	public Map<Integer,Integer> giveupQuestGoodsMap() {
    	//用户自己去丢弃相关物品
		return goodsMap ;
	}
  
    @Override
	public void giveUp(RoleInstance role) {
    	Map<Integer,Integer> goodsMap = giveupQuestGoodsMap();
    	Map<Integer,Integer> delMap = new HashMap<Integer,Integer>();
    	for(Integer goodsId : goodsMap.keySet()){
			GoodsBase goods = GameContext.getGoodsApp().getGoodsBase(goodsId.intValue());
			if(goods == null || !(goods instanceof GoodsTask)){
				continue;
			}
			delMap.put(goodsId, goodsMap.get(goodsId));
    	}
    	if(!Util.isEmpty(delMap)){
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
			GoodsTerm thisTerm = (GoodsTerm)term ;
			return this.getPoint(thisTerm.getMapId(), thisTerm.getNpcId());
		}
		return null ;
	}
}
