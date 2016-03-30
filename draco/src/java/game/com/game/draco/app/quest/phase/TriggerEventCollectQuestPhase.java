package com.game.draco.app.quest.phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.TriggerEventCollectTerm;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

/**机关触发收集阶段**/
public class TriggerEventCollectQuestPhase extends QuestPhaseAdator {

	private Map<String,List<Item>> tiggerMap = new HashMap<String,List<Item>>();
	private Map<Integer,Integer> goodsCountMap = new HashMap<Integer,Integer>();
	
	public TriggerEventCollectQuestPhase(String eventId, int goodsId, int goodsCount, int goodsProb, String mapId){
		this(eventId, goodsId, goodsCount, goodsProb, mapId,
			 null, 0, 0, 0, null,
			 null, 0, 0, 0, null);
	}
	
	public TriggerEventCollectQuestPhase(
			String eventId1, int goodsId1, int goodsCount1, int goodsProb1, String mapId1,
			String eventId2, int goodsId2, int goodsCount2, int goodsProb2, String mapId2){
		this(eventId1, goodsId1, goodsCount1, goodsProb1, mapId1,
			 eventId2, goodsId2, goodsCount2, goodsProb2, mapId2,
			 null, 0, 0, 0, null);
	}
	
	public TriggerEventCollectQuestPhase(
			String eventId1, int goodsId1, int goodsCount1, int goodsProb1, String mapId1,
			String eventId2, int goodsId2, int goodsCount2, int goodsProb2, String mapId2,
			String eventId3, int goodsId3, int goodsCount3, int goodsProb3, String mapId3){
		this.init(eventId1, goodsId1, goodsCount1, goodsProb1, mapId1);
		this.init(eventId2, goodsId2, goodsCount2, goodsProb2, mapId2);
		this.init(eventId3, goodsId3, goodsCount3, goodsProb3, mapId3);
	}
	
	private void init(String eventId, int goodsId, int goodsCount, int goodsProb, String mapId){
		if(Util.isEmpty(eventId) || goodsId <= 0){
			return;
		}
		if(!this.tiggerMap.containsKey(eventId)){
			this.tiggerMap.put(eventId, new ArrayList<Item>());
		}
		this.tiggerMap.get(eventId).add(new Item(goodsId, goodsCount, goodsProb));
		Util.mergerMap(this.goodsCountMap, goodsId, goodsCount);
		this.questTermList.add(new TriggerEventCollectTerm(QuestTermType.CollectPoint, goodsCount, eventId, goodsId, mapId));
	}
	
	@Override
	public int getGoods(RoleInstance role, int goodsId,int goodsNum){
		if(!goodsCountMap.containsKey(goodsId)){
			return 0;
		}
		int index = startIndex ;
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
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum , term, index);
			return 1;
		}
		return 0 ;
	}
	
	@Override
	public int getCurrentNum(RoleInstance role,int index){
		int start = startIndex ;
		for(QuestTerm term : this.questTermList){
			start ++ ;
			if(start == index){
				return role.getRoleBackpack().countByGoodsId(Integer.parseInt(term.getParameter()));
			}
		}
		return 0 ;
	}

	private int getGoodsIdIndex(int goodsId){
		if(!goodsCountMap.containsKey(goodsId)){
			return -1 ;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(term.getParameter().equals(String.valueOf(goodsId))){
				return index ;
			}
		}
		return -1 ;
	}

	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role, String key) {
		if(!tiggerMap.containsKey(key)){
			return null ;
		}
		List<Item> list = tiggerMap.get(key);
		if(Util.isEmpty(list)){
			return null ;
		}
		List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
		for(Item item : list){
			int goodsId = item.getGoodsId();
			int index = this.getGoodsIdIndex(goodsId);
			if(index < 0){
				continue ;
			}
			// 获得当前阶段数目
			int nowNum = this.getCurrentNum(role, index);
			if (nowNum >= goodsCountMap.get(goodsId)) {
				// 此种已经满足数量
				continue ;
			}
			//计算概率
			if(ProbabilityMachine.isProbability(item.goodsProb * ProbabilityMachine.RATE_MODULUS_HUNDRED_MULTI )){
				goodsList.add(new GoodsOperateBean(goodsId, 1, BindingType.template));
			}
		}
		return goodsList;
	}
	
	@Override
	public Map<Integer,Integer> submitQuestGoodsMap() {
		return goodsCountMap ;
	}

    @Override
	public Map<Integer,Integer> giveupQuestGoodsMap() {
    	//用户自己去丢弃相关物品
		return goodsCountMap;
	}
	
	public class Item {
		private int goodsId ;
		private int goodsCount ;
		private int goodsProb ;

		public int getGoodsId() {
			return goodsId;
		}

		public void setGoodsId(int goodsId) {
			this.goodsId = goodsId;
		}

		public int getGoodsCount() {
			return goodsCount;
		}

		public void setGoodsCount(int goodsCount) {
			this.goodsCount = goodsCount;
		}

		public int getGoodsProb() {
			return goodsProb;
		}

		public void setGoodsProb(int goodsProb) {
			this.goodsProb = goodsProb;
		}

		public Item(int goodsId,int goodsCount,int goodsProb){
			this.goodsId = goodsId ;
			this.goodsCount = goodsCount ;
			this.goodsProb = goodsProb ;
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
    	if(!Util.isEmpty(delMap)){
    		GameContext.getUserGoodsApp().deleteSomeForBagByMap(role, delMap, OutputConsumeType.quest_giveup_consume);
        }
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
			TriggerEventCollectTerm thisTerm = (TriggerEventCollectTerm)term ;
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
