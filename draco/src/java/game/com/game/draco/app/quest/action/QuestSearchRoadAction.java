package com.game.draco.app.quest.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhase;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0707_QuestSearchRoadReqMessage;

public class QuestSearchRoadAction extends BaseAction<C0707_QuestSearchRoadReqMessage>{

	@Override
	public Message execute(ActionContext context, C0707_QuestSearchRoadReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			int questId = reqMsg.getQuestId();
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			if(null == quest){
				return new C0003_TipNotifyMessage(Status.Quest_Not_Exist.getTips());
			}
			QuestStatus questStatus = QuestStatus.noneTask;
			//未接取的任务，不能寻路（客户端可自己寻到接取任务的地方）
			RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
			if(null != logInfo){
				questStatus = QuestHelper.getQuestStatus(role, quest, logInfo);
			}
			if(QuestStatus.noneTask == questStatus || QuestStatus.canAccept == questStatus){
				return this.acceptOrSubmitSearchRoad(role, quest.getAcceptMapId());
			}
			if(QuestStatus.canSubmit == questStatus){
				return this.acceptOrSubmitSearchRoad(role, quest.getSubmitMapId());
			}
			//已经失败的任务，不能寻路
			if(QuestStatus.failure == questStatus){
				return new C0003_TipNotifyMessage(Status.Quest_Search_Status_Fail.getTips());
			}
			//如果是活动地图的任务，寻路逻辑
			if(quest.isInActive()){
				return this.questSignTypeOfActive(role, quest);
			}
			//未完成的任务寻路
			if(QuestStatus.notComplete == questStatus){
				/*//根据不同的任务条件，进行相关的处理
				QuestPhase phase = quest.getCurrentPhase(role);
				if(null == phase){
					return null;
				}
				//副本通关、副本地图通关、地图刷怪波次
				if(phase instanceof CopyPassQuestPhase || phase instanceof CopyMapPassQuestPhase || phase instanceof MapRefreshQuestPhase){
					return this.questCopyMapSearchRoad(phase, role);
				}*/
				return this.currPhaseSearchRoad(role, quest);
			}
			return null;
		} catch (Exception e) {
			this.logger.error("QuestSearchRoadAction error: ", e);
			return null;
		}
	}
	
	/**
	 * 活动中的任务寻路
	 * @param role
	 * @param quest
	 * @return
	 */
	private Message questSignTypeOfActive(RoleInstance role, Quest quest){
		short activeId = quest.getActiveId();
		if(activeId <= 0){
			return null;
		}
		Active active = GameContext.getActiveApp().getActive(activeId);
		if(null == active){
			return null;
		}
		//活动开启，打开活动面板定位到活动
		if(active.isTimeOpen()){
			return GameContext.getActiveApp().createActivePanelListMsg(role);
		}
		//活动未开启，提示活动尚未开启
		return new C0003_TipNotifyMessage(Status.Quest_Search_Active_Not_Open.getTips());
	}
	
	/**
	 * 当前任务阶段寻路
	 * @param role
	 * @param quest
	 * @return
	 */
	private Message currPhaseSearchRoad(RoleInstance role, Quest quest){
		QuestPhase phase = quest.getCurrentPhase(role);
		if(null == phase){
			return null;
		}
		List<QuestTerm> termList = phase.termList();
		if(Util.isEmpty(termList)){
			return null;
		}
		QuestTerm term = null;
		for(int i=0; i<termList.size(); i++){
			QuestTerm t = termList.get(i);
			if(null == t){
				continue;
			}
			//已经完成该条件
			if(t.getCurrCount(role, phase.getMaster(), i) >= t.getCount()){
				continue;
			}
			term = t;
			break;
		}
		if(null == term){
			return null;
		}
		String mapId = term.getMapId();
		if(Util.isEmpty(mapId)){
			return null;
		}
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			return null;
		}
		short copyId = map.getMapConfig().getCopyId();
		if(copyId > 0 ){
			return this.copyMapSearchRoad(role, copyId);
		}
		return null;
	}
	
	/** 副本地图寻路 **/
	private Message copyMapSearchRoad(RoleInstance role, short copyId){
		//当前所在地图
		MapInstance nowMapIns = role.getMapInstance();
		if(null == nowMapIns){
			return null;
		}
		//当前在副本或通天塔，不需要做处理
		MapConfig nowInMapConfig = nowMapIns.getMap().getMapConfig();
		if(MapLogicType.copyLogic == nowInMapConfig.getMapLogicType()){
			return null ;
		}
		if(copyId == nowInMapConfig.getCopyId()){
			//已经在目标副本了，不需要做处理
			return null ;
		}
		//打开副本面板，定位到目标副本
		return GameContext.getCopyLogicApp().getCopyPanelRespMessage(role, copyId, CopyType.personal.getType());// 剧情副本属于普通类型
	}
	
	/** 接、交任务阶段寻路 **/
	private Message acceptOrSubmitSearchRoad(RoleInstance role, String mapId){
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			return null;
		}
		short copyId = map.getMapConfig().getCopyId();
		if(copyId > 0 ){
			return this.copyMapSearchRoad(role, copyId);
		}
		return null;
	}
	
	/**
	 * 副本杀怪、副本地图任务的寻路
	 * @param phase
	 * @param role
	 * @return
	 */
	/*private Message questCopyMapSearchRoad(QuestPhase phase, RoleInstance role){
		//目前所在地图
		MapInstance nowMapIns = role.getMapInstance();
		if(null == nowMapIns){
			return null;
		}
		//当前在副本或通天塔，不需要做处理
		MapConfig nowInMapConfig = nowMapIns.getMap().getMapConfig();
		if(MapLogicType.copyLogic == nowInMapConfig.getMapLogicType()){
			return null ;
		}
		List<QuestTerm> termList = phase.termList();
		if(Util.isEmpty(termList)){
			return null;
		}
		QuestTerm term = null;
		for(int i=0; i<termList.size(); i++){
			QuestTerm t = termList.get(i);
			if(null == t){
				continue;
			}
			//已经完成该条件
			if(t.getCurrCount(role, phase.getMaster(), i) >= t.getCount()){
				continue;
			}
			term = t;
			break;
		}
		if(null == term){
			return null;
		}
		String mapId = null;
		if(term instanceof KillMonsterTerm){
			KillMonsterTerm thisTerm = (KillMonsterTerm)term;
			mapId = thisTerm.getMapId();
		}else if(term instanceof MapTerm){
			MapTerm mapTerm = (MapTerm)term;
			mapId = mapTerm.getMapId();
		}else if(term instanceof MapRefreshTerm){
			MapRefreshTerm refreshTerm = (MapRefreshTerm)term;
			mapId = refreshTerm.getMapId();
		}
		if(Util.isEmpty(mapId)){
			return null;
		}
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			return null;
		}
		short copyId = map.getMapConfig().getCopyId();
		if(copyId > 0 ){
			if(copyId == nowInMapConfig.getCopyId()){
				//已经在目标副本了，不需要做处理
				return null ;
			}
			//打开副本面板，定位到目标副本
			return GameContext.getCopyLogicApp().getCopyPanelRespMessage(role, copyId);
		}
		return null;
	}*/
	
}
