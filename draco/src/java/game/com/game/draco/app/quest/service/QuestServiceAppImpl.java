package com.game.draco.app.quest.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestApp;
import com.game.draco.app.quest.base.QuestType;

public class QuestServiceAppImpl implements QuestServiceApp {
	
	private QuestApp questApp;
	private final Logger loggor = LoggerFactory.getLogger(this.getClass());
	private int firstMainQuestId = 0;
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		//初始化主线任务链
		this.initMainLineChain();
		//初始化任务接取限制
		this.initQuestVisible();
		//初始化任务道具 
		this.initRelyGoodsForQuest();
		//初始化任务上的活动ID
		this.initActiveLimit();
	}

	@Override
	public void stop() {
		
	}

	public QuestApp getQuestApp() {
		return questApp;
	}

	public void setQuestApp(QuestApp questApp) {
		this.questApp = questApp;
	}
	
	private void initMainLineChain(){
		String fileName = XlsSheetNameType.quest_main_line_chain.getXlsName();
		String sheetName = XlsSheetNameType.quest_main_line_chain.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> list = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			if(Util.isEmpty(list)){
				this.checkFail(info + "main line chain is not config!");
				return;
			}
			int size = list.size();
			this.firstMainQuestId = Integer.valueOf(list.get(0));
			if(this.firstMainQuestId <= 0){
				this.checkFail(info + "the first MainLine questId is error!");
			}
			for(int i=0; i<size; i++){
				int questId = Integer.valueOf(list.get(i));
				Quest quest = GameContext.getQuestApp().getQuest(questId);
				if(null == quest){
					this.checkFail(info + "questId = " + questId + ", quest is not exist!");
					continue;
				}
				if(!quest.isMainLine()){
					this.checkFail(info + "questId = " + questId + ", it's not MainLine quest!");
					continue;
				}
				//节点索引从1开始
				quest.setChainIndex(i + 1);
				int pre = i - 1;
				if(pre >= 0){
					int premiseQuestId = Integer.valueOf(list.get(pre));
					quest.setPremiseQuestId(premiseQuestId);
				}
				int next = i + 1;
				if(next < size){
					int nextQuestId = Integer.valueOf(list.get(next));
					quest.setNextQuestId(nextQuestId);
				}
			}
			boolean isError = false;
			StringBuffer buff = new StringBuffer();
			buff.append("The MainLine quest is not in the main_line_chain. questIds : ");
			//所有主线任务必须都在任务链中
			for(Quest quest : this.questApp.getAllQuest()){
				if(null == quest){
					continue;
				}
				if(QuestType.MainLine != quest.getQuestType()){
					continue;
				}
				//不在任务链中的主线任务
				if(quest.getChainIndex() <= 0){
					isError = true;
					buff.append(quest.getQuestId()).append(", ");
				}
			}
			if(isError){
				this.checkFail(buff.toString());
			}
		} catch (Exception e){
			this.checkFail(info, e);
		}
	}
	
	/** 
	 * 初始化任务上的道具ID[道具触发的任务]
	 */
	private void initRelyGoodsForQuest(){
		try {
			for(GoodsBase goodsBase : GameContext.getGoodsLoader().getDataMap().values()){
				if(null == goodsBase || goodsBase.getGoodsType() != GoodsType.GoodsTaskprops.getType()){
					continue;
				}
				GoodsTaskprops taskprops = (GoodsTaskprops) goodsBase;
				Quest quest = this.questApp.getQuest(taskprops.getTaskId());
				if(null == quest){
					continue;
				}
				quest.setRelyGoodsId(taskprops.getId());
			}
		} catch (Exception e){
			this.loggor.error("QuestServiceApp.initRelyGoodsForQuest() Error:", e);
		}
	}
	
	/**
	 * 初始化任务接取限制
	 */
	private void initQuestVisible(){
		String fileName = XlsSheetNameType.quest_visible.getXlsName();
		String sheetName = XlsSheetNameType.quest_visible.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<QuestVisible> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, QuestVisible.class);
			if(Util.isEmpty(list)){
				return;
			}
			for(QuestVisible visible : list){
				if(null == visible){
					continue;
				}
				int questId = visible.getQuestId();
				Quest quest = this.questApp.getQuest(questId);
				if(null == quest){
					this.checkFail(info + "questId=" + questId + ",This quest is not exsit!");
					continue;
				}
				//初始化参数
				visible.init();
				//赋值
				quest.setRegChannelSet(visible.getRegChannelSet());
				quest.setLoginChannelSet(visible.getLoginChannelSet());
				quest.setVipLevelSet(visible.getVipLevelSet());
			}
		} catch (Exception e){
			this.checkFail(info, e);
		}
	}
	
	/**
	 * 加载任务的活动ID
	 */
	private void initActiveLimit(){
		String fileName = XlsSheetNameType.quest_active_limit.getXlsName();
		String sheetName = XlsSheetNameType.quest_active_limit.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<QuestActiveLimit> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, QuestActiveLimit.class);
			if(Util.isEmpty(list)){
				return;
			}
			for(QuestActiveLimit config : list){
				if(null == config){
					continue;
				}
				int questId = config.getQuestId();
				Quest quest = this.questApp.getQuest(questId);
				if(null == quest){
					this.checkFail(info + "questId=" + questId + ",This quest is not exsit!");
					continue;
				}
				short activeId = config.getActiveId();
				Active active = GameContext.getActiveApp().getActive(activeId);
				if(null == active){
					this.checkFail(info + "questId=" + questId + ",activeId=" + activeId + ",This active is not exsit!");
					continue;
				}
				//同一个任务不能在多个活动中
				if(quest.getActiveId() > 0){
					this.checkFail(info + "questId=" + questId + ",This quest has active: activeId=" + quest.getActiveId());
					continue;
				}
				quest.setActiveId(activeId);
			}
		} catch (Exception e){
			this.checkFail(info, e);
		}
	}
	
	private void checkFail(String info, Exception e){
		Log4jManager.CHECK.error(info, e);
		Log4jManager.checkFail();
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public int getFirstMainQuestId() {
		return this.firstMainQuestId;
	}
	
}
