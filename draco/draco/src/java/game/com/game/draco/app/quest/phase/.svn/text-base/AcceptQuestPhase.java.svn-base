package com.game.draco.app.quest.phase;

import java.util.HashMap;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class AcceptQuestPhase extends QuestPhaseAdator{
	
	private String npcId;
	private int letterGoodsId;
	
	@Override
	public Point getEventPoint(RoleInstance role) {
		return this.getPoint(mapId, npcId);
	}
	
	public AcceptQuestPhase(String npcId) {
		this(npcId,0);
	}
	
	public AcceptQuestPhase(String npcId, int letterGoodsId) {
		this.npcId = npcId;
		this.letterGoodsId = letterGoodsId;
		this.hasGoodsEffect = this.letterGoodsId > 0 ;
	}
	
	public AcceptQuestPhase(String npcId, String mapId){
		this(npcId, 0, mapId);
	}
	
	public AcceptQuestPhase(String npcId, int letterGoodsId, String mapId){
		this.npcId = npcId;
		this.letterGoodsId = letterGoodsId;
		this.hasGoodsEffect = this.letterGoodsId > 0 ;
		this.mapId = mapId;
	}
	
	public String getNpcId() {
		return npcId;
	}

	public void setNpcId(String npcId) {
		this.npcId = npcId;
	}

	public int getLetterGoodsId() {
		return letterGoodsId;
	}

	public void setLetterGoodsId(int letterGoodsId) {
		this.letterGoodsId = letterGoodsId;
	}
	
	
	@Override
	public boolean isPhaseComplete(RoleInstance role) {
		return true ;
	}

    @Override
	public int completePhaseAction(RoleInstance role){
		if(letterGoodsId <=0){
			return 1;
		}
		//将信件放入用户背包
		GoodsResult result = GameContext.getUserGoodsApp().addGoodsForBag(role, this.letterGoodsId, 1, OutputConsumeType.quest_award);
		if(result.isSuccess()){
			return 1;
		}
		return 0;
	}
    
    @Override
	public int getGoods(RoleInstance role, int goodsId, int goodsNum) {
    	if(this.letterGoodsId <=0){
    		return 0 ;
    	}
    	if(this.letterGoodsId != goodsId){
    		return 0 ;
    	}
    	if(QuestHelper.isLetterGoodsExists(role, this.master)){
    		return 1 ;
    	}
		return 0 ;
	}
	
	@Override
	public Map<Integer,Integer> submitQuestGoodsMap() {
		if(letterGoodsId <= 0){
			return null ;
		}
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		map.put(letterGoodsId, 1);
		return map ;
	}

    @Override
	public Map<Integer,Integer> giveupQuestGoodsMap() {
		return submitQuestGoodsMap();
	}
    
    @Override
	public void giveUp(RoleInstance role) {
    	Map<Integer,Integer> goodsMap =  giveupQuestGoodsMap();
    	if(Util.isEmpty(goodsMap)){
    		return ;
    	}
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
}
