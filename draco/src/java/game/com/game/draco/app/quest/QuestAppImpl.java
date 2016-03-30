package com.game.draco.app.quest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.QuestAwardDetail;

import com.game.draco.GameContext;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.app.quest.config.QuestConfig;
import com.game.draco.app.quest.config.QuestXlsType;
import com.game.draco.app.quest.phase.AcceptQuestPhase;
import com.game.draco.app.quest.phase.SubmitQuestPhase;

public class QuestAppImpl extends QuestApp {
	
	private final static Logger logger = LoggerFactory.getLogger(QuestAppImpl.class);
	private PathConfig pathConfig;
	private ScriptSupport scriptSupport;
	
	private void initQuestAward(){
		//加载任务奖励
		String fileName = XlsSheetNameType.quest_award.getXlsName();
		String sheetName = XlsSheetNameType.quest_award.getSheetName();
		String sourceFile = pathConfig.getXlsPath() + fileName;
		Map<String,QuestAwardDetail> awardDetailMap  = XlsPojoUtil.sheetToMap(sourceFile, sheetName, QuestAwardDetail.class);
		Map<Integer, QuestAward> awardMap = this.buildQuestAward(awardDetailMap);
		for(Quest quest : questMap.values()){
			if(null == quest){
				continue;
			}
			QuestAward award = awardMap.get(quest.getQuestId());
			if(null == award){
				logger.warn("quest:" + quest.getQuestId() + " not config any award");
			}
			//为null时也设置
			quest.setAward(award);
		}
		awardDetailMap.clear();
		awardDetailMap = null ;
		awardMap.clear();
		awardMap = null ;
	}
	
	@Override
	public void start() {
		//加载任务脚本
		scriptSupport.loadScript(pathConfig.getQuestPath(),true);
		//加载xls表中的任务
		this.loadXlsQuest();
		//加载任务奖励
		this.initQuestAward();
		//容错,写脚本人员常忘记将日常任务设置了可循环
		for(Quest quest : questMap.values()){
			if(null != quest.getQuestType() 
					&& QuestType.Daily == quest.getQuestType()){
				quest.setCanRepeat(true);
			}
			List<QuestPhase> ql = quest.getPhaseList();
			for(QuestPhase q:ql){
				if(q instanceof AcceptQuestPhase){
					quest.setAcceptNpcId(((AcceptQuestPhase)q).getNpcId());
				}else if(q instanceof SubmitQuestPhase){
					quest.setSubmitNpcId(((SubmitQuestPhase)q).getNpcId());
				}
			}
		}
	}
	
	private Map<Integer, QuestAward> buildQuestAward(Map<String,QuestAwardDetail> awardDetailMap) {
		Map<Integer, QuestAward> questAwardMap = new HashMap<Integer, QuestAward>();
		for(QuestAwardDetail detail : awardDetailMap.values()){
			if(null == detail){
				continue;
			}
			questAwardMap.put(detail.getId(), detail.getQuestAward());
		}
		return questAwardMap;
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public Quest getQuest(int questId)  {
		return questMap.get(questId);
	}

	@Override
	public Collection<Quest> getAllQuest() {
		return questMap.values();
	}
	
	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}

	public void setScriptSupport(ScriptSupport scriptSupport) {
		this.scriptSupport = scriptSupport;
	}

	@Override
	public void setArgs(Object args) {
		// TODO Auto-generated method stub
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	@Override
    public boolean beClosed(int questId){
    	String closedIds = GameContext.getParasConfig().getClosedQuestIds();
    	if(Util.isEmpty(closedIds)){
    		return false;
    	}
    	String checkIds = "," + closedIds + ",";
    	if(checkIds.indexOf("," + questId + ",") < 0){
    		return false;
    	}
    	return true;
    }
	
	/**
	 * 加载xls中的任务
	 */
	private void loadXlsQuest(){
		String fileName = XlsSheetNameType.quest_config_list.getXlsName();
		for(QuestXlsType questXlsType : QuestXlsType.values()){
			if(null == questXlsType){
				continue;
			}
			this.loadQuestConfig(fileName,questXlsType);
		}
	}
	
	/**
	 * 加载xls中的任务
	 * @param fileName
	 * @param questXlsType
	 */
	private void loadQuestConfig(String fileName, QuestXlsType questXlsType){
		String sheetName = questXlsType.getSheetName();
		try {
			String sourceFile = this.pathConfig.getXlsPath() + fileName;
			Map<String,QuestConfig> questMap = XlsPojoUtil.sheetToMap(sourceFile, sheetName, QuestConfig.class);
			for(QuestConfig config : questMap.values()){
				if(null == config){
					continue;
				}
				//初始化任务配置
				config.init(questXlsType);
				//初始化任务（跟初始化任务脚本一样）
				registerQuest(config.getQuest());
			}
		} catch (Exception e) {
			this.checkFail("fileName=" + fileName + ",sheetName=" + sheetName + ".");
		}
	}
	
}
