package com.game.draco.app.quest.phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestPhase;
import com.game.draco.app.quest.QuestPhaseAdator;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class ComposeQuestPhase extends QuestPhaseAdator{

	//合成阶段有2个缺点
	//1:最多只能有5个子阶段
	//2:每个子阶段只能有1个条件,因为
	private List<QuestPhase> phaseSet = new ArrayList<QuestPhase>();
	private Map<Integer, Integer> submitQuestGoodsMap = new HashMap<Integer, Integer>();
	
	public ComposeQuestPhase(QuestPhaseAdator phase1){
		this(phase1, null, null, null, null);
	}
	
	public ComposeQuestPhase(QuestPhaseAdator phase1, 
			QuestPhaseAdator phase2){
		this(phase1, phase2, null, null, null);
	}
	
	public ComposeQuestPhase(QuestPhaseAdator phase1,
			QuestPhaseAdator phase2,
			QuestPhaseAdator phase3){
		this(phase1, phase2, phase3, null, null);
	}
	
	public ComposeQuestPhase(QuestPhaseAdator phase1,
			QuestPhaseAdator phase2,
			QuestPhaseAdator phase3,
			QuestPhaseAdator phase4){
		this(phase1, phase2, phase3, phase4, null);
	}
	
	public ComposeQuestPhase(QuestPhaseAdator phase1,
			QuestPhaseAdator phase2,
			QuestPhaseAdator phase3,
			QuestPhaseAdator phase4,
			QuestPhaseAdator phase5){
		init(phase1);
		init(phase2);
		init(phase3);
		init(phase4);
		init(phase5);
		
		for (QuestPhase phase : phaseSet) {
			//hasGoodsEffect必须重新设置值
			this.hasGoodsEffect = this.hasGoodsEffect || phase.isHasGoodsEffect();
			submitQuestGoodsMap = Util.mergerMap(submitQuestGoodsMap, phase.submitQuestGoodsMap());
		}
	}
	
	private void init(QuestPhaseAdator phase){
		if(phase!=null){
			phase.setStartIndex(startIndex);
			startIndex+=phase.termList().size();
			if(startIndex>=5 || phase.termList().size() > 1){
				throw new RuntimeException("组合阶段不允许超过5个分阶段并且每分阶段条件不能大于1!");
			}
			//将所有子阶段的条件放进到父阶段
			phaseSet.add(phase);
			this.questTermList.addAll(phase.termList());
		}
	}
	
	@Override
	public int triggerEvent(RoleInstance role,String eventId) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.triggerEvent(role, eventId);
		}
		return ret ;
	}

	@Override
	public int death(RoleInstance role) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.death(role);
		}
		return ret ;
	}

	@Override
	public int enterMap(RoleInstance role) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.enterMap(role);
		}
		return ret ;
	}

	@Override
	public int getGoods(RoleInstance role, int goodsId,int goodsNum) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.getGoods(role, goodsId,goodsNum);
		}
		return ret ;
	}
	@Override
	public int getAttribute(RoleInstance role, int type) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.getAttribute(role, type);
		}
		return ret ;
	}

	@Override
	public int killMonster(RoleInstance role, String npcId) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.killMonster(role, npcId);
		}
		return ret ;
	}
	
	@Override
	public int talkNpc(RoleInstance role, String npcId) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.talkNpc(role, npcId);
		}
		return ret ;
	}

	@Override
	public int useGoods(RoleInstance role, int goodsId) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.useGoods(role, goodsId);
		}
		return ret ;
	}
	
	@Override
	public void setMaster(Quest master) {
		this.master = master;
		for(QuestPhase phase : phaseSet){
			phase.setMaster(master);
		}
	}

	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role, String npcId) {
		List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
		for(QuestPhase phase : phaseSet){
			List<GoodsOperateBean> list = phase.getQuestFall(role, npcId);
			if(Util.isEmpty(list)){
				continue;
			}
			goodsList.addAll(list);
		}
		return goodsList;
	}
	
	@Override
	public Map<Integer, Integer> submitQuestGoodsMap() {
		return submitQuestGoodsMap ;
	}

	@Override
	public Map<Integer, Integer> giveupQuestGoodsMap() {
		//用户自己去丢弃相关物品
    	return null ;
	}
	
	@Override
	public int getCurrentNum(RoleInstance role,int index){
		QuestPhase phase = phaseSet.get(index);
		return phase.getCurrentNum(role, index);
	}
	
	 @Override
	public void giveUp(RoleInstance role) {

		for(QuestPhase phase : phaseSet){
				phase.giveUp(role);
		}
	}
	 
	@Override
	public int killRole(RoleInstance role) {
		int ret = 0 ;
		for(QuestPhase phase : phaseSet){
			ret += phase.killRole(role);
		}
		return ret ;
	}
	
	@Override
	public int chooseMenu(RoleInstance role, int menuId) {
		int ret = 0;
		for(QuestPhase phase : phaseSet){
			ret += phase.chooseMenu(role, menuId);
		}
		return ret;
	}
	
	@Override
	public int killMonsterLimit(RoleInstance role, String npcId) {
		int ret = 0;
		for(QuestPhase phase : phaseSet){
			ret += phase.killMonsterLimit(role, npcId);
		}
		return ret;
	}
	
	@Override
	public boolean isSpecialCurrCount() {
		//组合阶段的条件数量需要特殊处理
		return true;
	}
	
	@Override
	public int copyPass(RoleInstance role, short copyId) {
		int ret = 0;
		for(QuestPhase phase : phaseSet){
			ret += phase.copyPass(role, copyId);
		}
		return ret;
	}
	
	@Override
	public int copyMapPass(RoleInstance role, String mapId) {
		int ret = 0;
		for(QuestPhase phase : phaseSet){
			ret += phase.copyMapPass(role, mapId);
		}
		return ret;
	}
	
	@Override
	public int killNpcFallCount(RoleInstance role, String npcId) {
		int ret = 0;
		for(QuestPhase phase : phaseSet){
			ret += phase.killNpcFallCount(role, npcId);
		}
		return ret;
	}
	
}
