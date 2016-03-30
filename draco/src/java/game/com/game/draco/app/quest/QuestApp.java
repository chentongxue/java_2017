package com.game.draco.app.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.app.quest.domain.RoleQuestDailyFinished;
import com.game.draco.app.quest.domain.RoleQuestFinished;
import com.game.draco.app.quest.phase.AcceptQuestPhase;
import com.game.draco.app.quest.phase.SubmitQuestPhase;
import com.google.common.collect.Maps;

public abstract class QuestApp implements Service{
	
	/** 任务集合 */
	protected static Map<Integer,Quest> questMap = Maps.newConcurrentMap();
	
	/** 从NPC身上接取的任务（查找可接任务时遍历此集合） */
	protected static List<Integer> npcAcceptQuestList = new ArrayList<Integer>();
	
	/**NPC所能接到的任务ID列表*/
	protected static Map<String,Set<Integer>> acceptNpcMapping = Maps.newConcurrentMap() ;
	
	/**NPC所能提交的任务ID列表*/
	protected static Map<String,Set<Integer>> submitNpcMapping = Maps.newConcurrentMap() ;
	
	public static void registerQuest(Quest quest){
		if(null == quest){
			return ;
		}
		//在注册任务的时候进行验证
		//避免运行时出现不必要异常
		int questId = quest.getQuestId();
		//验证任务脚本
		quest.verify();
		//任务ID不能重复
		if(questMap.containsKey(questId)){
			Log4jManager.CHECK.error("questId=" + questId + "the quest is exist.");
			Log4jManager.checkFail();
			return;
		}
		QuestType questType = quest.getQuestType();
		//验证主线、支线和日常类型的任务ID范围是否合法
		if(QuestType.MainLine == questType){
			//主线支线任务ID范围（1~3199）
			if(questId <= 0 || questId > RoleQuestFinished.Max_QuestId){
				Log4jManager.CHECK.error("questType=" + questType + ",questId=" + questId + ",the questId is error.");
				Log4jManager.checkFail();
				return;
			}
		}
		//日常任务ID范围（10001~12559）
		if(QuestType.Daily == questType){
			if(questId <= RoleQuestDailyFinished.Min_QuestId || questId > RoleQuestDailyFinished.Max_QuestId){
				Log4jManager.CHECK.error("questType=" + questType + ",questId=" + questId + ",the questId is error.");
				Log4jManager.checkFail();
				return;
			}
		}
		//放入任务缓存中
		questMap.put(questId, quest);
		//这步骤不能少
		quest.init();
		//主线、支线和日常的任务才放到NPC身上
		if(questType.isPutNpc()){
			for(QuestPhase phase : quest.getPhaseList()){
				if(phase instanceof AcceptQuestPhase){
					//接任务NPC匹配
					AcceptQuestPhase current = (AcceptQuestPhase)phase ;
					String npcId = current.getNpcId();
					//NPC的ID为空，就不需要往里放了
					if(Util.isEmpty(npcId)){
						continue;
					}
					if(!acceptNpcMapping.containsKey(npcId)){
						acceptNpcMapping.put(npcId, new HashSet<Integer>());
					}
					acceptNpcMapping.get(npcId).add(questId);
					//添加到NPC接取任务的集合中
					npcAcceptQuestList.add(questId);
					continue;
				}
				if(phase instanceof SubmitQuestPhase){
					SubmitQuestPhase current = (SubmitQuestPhase)phase ;
					String npcId = current.getNpcId();
					//NPC的ID为空，就不需要往里放了
					if(Util.isEmpty(npcId)){
						continue;
					}
					if(!submitNpcMapping.containsKey(npcId)){
						submitNpcMapping.put(npcId, new HashSet<Integer>());
					}
					submitNpcMapping.get(npcId).add(questId);
				}
			}
		}
	}
	
	public abstract Quest getQuest(int questId) ;

	public Map<String, Set<Integer>> getAcceptNpcMapping() {
		return acceptNpcMapping;
	}

	public Map<String, Set<Integer>> getSubmitNpcMapping() {
		return submitNpcMapping;
	}
	
	public abstract Collection<Quest> getAllQuest() ;
	
	public boolean isQuestNpc(NpcInstance npc) {
		if (null == npc) {
			return false;
		}
		NpcTemplate npcTemplate = npc.getNpc();
		if (null == npcTemplate) {
			return false;
		}
		return this.isQuestNpc(npcTemplate.getNpcid());
	}
	
	public boolean isQuestNpc(String npcTemplateId) {
		if (null == npcTemplateId || 0 == npcTemplateId.trim().length()) {
			return false;
		}
		return acceptNpcMapping.containsKey(npcTemplateId)
				|| submitNpcMapping.containsKey(npcTemplateId);
	}
	
	/** 任务是否被关闭 */
	public abstract boolean beClosed(int questId);

	/**
	 * NPC身上可接取的任务集合
	 * 可接任务列表遍历此集合
	 * @return
	 */
	public static List<Integer> getNpcAcceptQuestList() {
		return npcAcceptQuestList;
	}
	
}
