package com.game.draco.app.quest.config;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestAdaptor;
import com.game.draco.app.quest.QuestPhase;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.app.quest.phase.AcceptQuestPhase;
import com.game.draco.app.quest.phase.ChooseMenuQuestPhase;
import com.game.draco.app.quest.phase.CopyMapPassQuestPhase;
import com.game.draco.app.quest.phase.CopyPassQuestPhase;
import com.game.draco.app.quest.phase.DiscoveryQuestPhase;
import com.game.draco.app.quest.phase.GoodsCollectQuestPhase;
import com.game.draco.app.quest.phase.KillMonsterLimitQuestPhase;
import com.game.draco.app.quest.phase.KillMonsterQuestPhase;
import com.game.draco.app.quest.phase.KillNpcFallQuestPhase;
import com.game.draco.app.quest.phase.KillRoleQuestPhase;
import com.game.draco.app.quest.phase.SubmitQuestPhase;
import com.game.draco.app.quest.phase.TriggerEventQuestPhase;
import com.game.draco.app.quest.phase.UseGoodsTypeQuestPhase;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

@Data
public class QuestConfig {
	
	private int questId;//任务ID
	private String questName;//任务名称
	private String questDesc;//任务描述
	private String targetDesc;//任务目标描述
	private String mapId;//阶段地图ID
	private int count;//数量
	private String npcId;//NPC/怪物ID
	private String menuId;//对话菜单ID
	private String eventId;//机关ID
	private int goodsId;//物品ID
	private int goodsType;//物品类型
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	private String goodsName;//物品名称
	private int dropProb;//掉落概率
	private short copyId;//副本ID
	private String copyName;//副本名称
	
	private QuestXlsType questXlsType;//任务配置类型
	
	/**
	 * 初始化任务配置
	 * @param questConfigType 任务阶段配置类型
	 */
	public void init(QuestXlsType questXlsType){
		this.questXlsType = questXlsType;
		//验证配置是否正确
		String info = "load quest_config_list error: sheetName=" + questXlsType.getSheetName() + ".questId=" + this.questId + ",";
		if(this.questId <= 0){
			this.checkFail(info + "the questId is error!");
		}
		switch(this.questXlsType){
		case KillMonster:
			//必须配怪的ID
			this.checkMonsterId(info);
			//必须配置数量
			this.checkCount(info);
			break;
		case ChooseMenu:
			if(Util.isEmpty(this.menuId)){
				this.checkFail(info + "menuId is not config!");
			}
			break;
		case TriggerEvent:
			if(Util.isEmpty(this.eventId)){
				this.checkFail(info + "triggerType is not config!");
			}
			//必须配置数量
			this.checkCount(info);
			break;
		case KillMonsterLimit:
			if(this.minLevel <= 0 || this.maxLevel <= 0 || this.minLevel > this.maxLevel){
				this.checkFail(info + "minLevel or maxLevel is config error!");
			}
			//必须配置数量
			this.checkCount(info);
			break;
		case NpcFall:
			//必须配怪的ID
			this.checkMonsterId(info);
			//必须配置数量
			this.checkCount(info);
			if(Util.isEmpty(this.goodsName)){
				this.checkFail(info + "goodsName is not config!");
			}
			if(this.dropProb <= 0){
				this.checkFail(info + "dropProb is error!");
			}
			break;
		case MapEnter:
			if(Util.isEmpty(this.mapId)){
				this.checkFail(info + "mapId is not config!");
			}
			break;
		case Goods:
			if(this.goodsId <= 0){
				this.checkFail(info + "goodsId is error!");
			}
			//必须配置数量
			this.checkCount(info);
			break;
		case CopyMapPass:
			if(this.minLevel <= 0 || this.maxLevel <= 0 || this.minLevel > this.maxLevel){
				this.checkFail(info + "minLevel or maxLevel is config error!");
			}
			//必须配置数量
			this.checkCount(info);
			break;
		case CopyPass:
			if(this.copyId <= 0 || Util.isEmpty(this.copyName)){
				this.checkFail(info + "copyId or copyName is config error!");
			}
			//必须配置数量
			this.checkCount(info);
			break;
		case UseGoodsType:
			if(this.goodsType <= 0){
				this.checkFail(info + ",goodsType is error.");
			}
			break;
		}
	}
	
	private void checkCount(String info){
		if(this.count <= 0){
			this.checkFail(info + "count=" + this.count + ",it's error!");
		}
	}
	
	private void checkMonsterId(String info){
		if(Util.isEmpty(this.npcId)){
			this.checkFail(info + "monsterId is not config!");
		}
	}
	
	/**
	 * 检测失败
	 * @param info
	 */
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 获取任务
	 * 根据配置信息构建任务对象
	 * @return
	 */
	public Quest getQuest(){
		Quest quest = new QuestAdaptor(this.questId);
		quest.setQuestName(this.questName);
		quest.setQuestDesc(this.questDesc);
		quest.setTargetDesc(this.targetDesc);
		//构建任务默认信息
		//Excel加载的任务设置为可重复的类型
		quest.setQuestType(QuestType.Repeat);
		quest.setCanRepeat(true);
		quest.setCanGiveUp(true);
		//构建任务阶段信息
		List<QuestPhase> phaseList = new ArrayList<QuestPhase>();
		//接任务NPC的ID为空，目的是让任务不在NPC身上显示
		AcceptQuestPhase acceptPhase = new AcceptQuestPhase("");
		//接任务NPC的ID为空，目的是让任务不在NPC身上显示
		SubmitQuestPhase submitPhase = new SubmitQuestPhase("");
		phaseList.add(acceptPhase);
		QuestPhase phase = this.getQuestPhase();
		if(null != phase){
			phaseList.add(phase);
		}
		phaseList.add(submitPhase);
		quest.setPhaseList(phaseList);
		return quest;
	}
	
	/**
	 * 构建任务阶段
	 * 根据任务类型构建不同的阶段
	 * @return
	 */
	private QuestPhase getQuestPhase(){
		switch(this.questXlsType){
		case KillMonster: 
			return new KillMonsterQuestPhase(this.npcId, this.count, this.mapId);
		case ChooseMenu:
			return new ChooseMenuQuestPhase(this.menuId, this.mapId, this.npcId);
		case TriggerEvent:
			return new TriggerEventQuestPhase(this.eventId, this.count, this.mapId);
		case KillMonsterLimit:
			return new KillMonsterLimitQuestPhase(this.minLevel, this.maxLevel, this.count, this.mapId);
		case NpcFall:
			return new KillNpcFallQuestPhase(this.npcId, this.goodsName, this.count, this.dropProb, this.mapId);
		case MapEnter:
			return new DiscoveryQuestPhase(this.mapId);
		case Goods:
			return new GoodsCollectQuestPhase(this.goodsId, this.count, this.mapId, this.npcId);
		case KillRole:
			return new KillRoleQuestPhase(this.count, this.mapId);
		case UseGoodsType:
			return new UseGoodsTypeQuestPhase(this.goodsType, this.count);
		case CopyPass:
			return new CopyPassQuestPhase(this.copyId, this.copyName);
		case CopyMapPass:
			return new CopyMapPassQuestPhase(this.mapId);
		}
		return null;
	}
	
}
