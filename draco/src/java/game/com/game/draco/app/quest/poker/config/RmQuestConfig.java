package com.game.draco.app.quest.poker.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.app.quest.base.QuestType;

public @Data class RmQuestConfig {
	
	private int setId;//集合ID
	private int quest1;//任务1
	private int quest2;//任务2
	private int quest3;//任务3
	private int quest4;//任务4
	private int quest5;//任务5
	private int quest6;//任务6
	private int quest7;//任务7
	private int quest8;//任务8
	private int quest9;//任务9
	private int quest10;//任务10
	private int quest11;//任务11
	private int quest12;//任务12
	private int quest13;//任务13
	private int quest14;//任务14
	private int quest15;//任务15
	private int quest16;//任务16
	private int quest17;//任务17
	private int quest18;//任务18
	private int quest19;//任务19
	private int quest20;//任务20
	private int quest21;//任务21
	private int quest22;//任务22
	private int quest23;//任务23
	private int quest24;//任务24
	private int quest25;//任务25
	
	private List<Integer> questList = new ArrayList<Integer>();
	private int questListSize = 0;//表中配置任务的数量
	/** 任务集合：KEY=任务ID,VALUE=任务对象 */
	private Map<Integer,Quest> questMap = new HashMap<Integer,Quest>();
	
	
	public void init(String fileInfo){
		try {
			String info = fileInfo + "setId=" + this.setId + ",";
			if(this.setId <= 0){
				this.checkFail(info);
			}
			this.addToQuestMap(info, this.quest1);
			this.addToQuestMap(info, this.quest2);
			this.addToQuestMap(info, this.quest3);
			this.addToQuestMap(info, this.quest4);
			this.addToQuestMap(info, this.quest5);
			this.addToQuestMap(info, this.quest6);
			this.addToQuestMap(info, this.quest7);
			this.addToQuestMap(info, this.quest8);
			this.addToQuestMap(info, this.quest9);
			this.addToQuestMap(info, this.quest10);
			this.addToQuestMap(info, this.quest11);
			this.addToQuestMap(info, this.quest12);
			this.addToQuestMap(info, this.quest13);
			this.addToQuestMap(info, this.quest14);
			this.addToQuestMap(info, this.quest15);
			this.addToQuestMap(info, this.quest16);
			this.addToQuestMap(info, this.quest17);
			this.addToQuestMap(info, this.quest18);
			this.addToQuestMap(info, this.quest19);
			this.addToQuestMap(info, this.quest20);
			this.addToQuestMap(info, this.quest21);
			this.addToQuestMap(info, this.quest22);
			this.addToQuestMap(info, this.quest23);
			this.addToQuestMap(info, this.quest24);
			this.addToQuestMap(info, this.quest25);
			//任务列表的长度
			this.questListSize = this.questList.size();
			if(0 == this.questListSize){
				this.checkFail(info + "questList is empty");
			}
		} catch (RuntimeException e) {
			this.checkFail("random quest init error:");
		}
	}
	
	private void addToQuestMap(String fileInfo, int questId){
		try {
			if(questId <= 0){
				return;
			}
			String info = fileInfo + "setId=" + this.setId + ",";
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			if(null == quest){
				this.checkFail(info + "questId=" + questId + ",not exist!");
				return ;
			}
			QuestAcceptType acceptType = quest.getQuestAcceptType();
			//同一种类型的随机任务中，同一个任务可以出现多次
			if(QuestAcceptType.Npc != acceptType && QuestAcceptType.Poker != acceptType){
				this.checkFail(info + "questId=" + questId + ",this quest has be used.acceptType=" + acceptType);
				return;
			}
			//随机任务设置为循环任务
			quest.setQuestType(QuestType.Repeat);
			//设置接任务类型（不可少）
			quest.setQuestAcceptType(QuestAcceptType.Poker);
			this.questList.add(questId);
			this.questMap.put(questId, quest);
		} catch (Exception e) {
			this.checkFail("random quest init error: addToQuestMap() questId=" + questId);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 随机获取任务ID
	 * @return
	 */
	public int getRandomQuestId(){
		if(this.questListSize <= 0){
			return 0;
		}
		int index = RandomUtil.randomInt(0, this.questListSize-1);
		return this.questList.get(index);
	}
	
}
