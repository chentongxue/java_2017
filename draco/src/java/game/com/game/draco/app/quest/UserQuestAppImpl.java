package com.game.draco.app.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.exception.GoodsIsOnlyException;
import sacred.alliance.magic.app.goods.exception.GoodsNotFoundException;
import sacred.alliance.magic.app.goods.exception.OutOfGoodsBagException;
import sacred.alliance.magic.app.map.point.CollectablePoint;
import sacred.alliance.magic.app.msgcenter.MessageCenter;
import sacred.alliance.magic.app.user.UserGoodsApp;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.CollectPointNotifyType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.MapConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.SortedValueNumMap;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.HatredTarget;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.drama.config.DramaTriggerType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.quest.base.NpcQuestHeadSign;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.app.quest.base.QuestListReqestType;
import com.game.draco.app.quest.base.QuestOpCode;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.app.quest.domain.RoleQuestDailyFinished;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.app.team.Team;
import com.game.draco.message.item.CollectPointIdItem;
import com.game.draco.message.item.CollectPointIdItem2;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.item.NpcHeadSignItem;
import com.game.draco.message.item.QuestGroupItem;
import com.game.draco.message.item.QuestSimpleInfoItem;
import com.game.draco.message.push.C0606_CollectPointNotifyMessage;
import com.game.draco.message.response.C0700_NpcHeadSignRespMessage;
import com.game.draco.message.response.C0701_QuestListRespMessage;
import com.game.draco.message.response.C0705_QuestSubmitRespMessage;
import com.game.draco.message.response.C0706_QuestGiveupRespMessage;

public class UserQuestAppImpl implements UserQuestApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private QuestApp questApp;
	private UserGoodsApp userGoodsApp;
	private BaseDAO baseDAO;
	private MessageCenter messageCenter;

	@Override
	public int onLogin(RoleInstance role, Object context){
		try {
			String roleId = role.getRoleId();
			List<RoleQuestLogInfo> list = this.baseDAO.selectList(RoleQuestLogInfo.class, "roleId", roleId);
			if (list != null) {
				for(RoleQuestLogInfo info : list){
					if(null == info){
						continue;
					}
					//必须设置库中已经存在
					info.setInDatabase(true);
					int questId = info.getQuestId();
					Quest quest = this.questApp.getQuest(questId);
					if(null == quest){
						continue;
					}
					role.getQuestLogMap().put(questId, info);
					if(quest.getTimeLimit() > 0){
						//限时任务
						role.getQuestTimeLimitSet().add(questId);
					}
				}
			}
			//日常任务已完成的任务日志
			RoleQuestDailyFinished dailyFinishLog = this.baseDAO.selectEntity(RoleQuestDailyFinished.class, RoleQuestDailyFinished.ROLEID, roleId);
			if(null == dailyFinishLog){
				dailyFinishLog = new RoleQuestDailyFinished();
				dailyFinishLog.setRoleId(roleId);
				dailyFinishLog.setDbStateCreate();
			}
			role.setQuestDailyFinished(dailyFinishLog);
		} catch (Exception e) {
			logger.error("task init load failed, ", e);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context){
		try {
			for(RoleQuestLogInfo info : role.getQuestLogMap().values()){
				try {
					if(null == info){
						continue;
					}
					if(info.isInDatabase()){
						this.baseDAO.update(info);
					}else{
						this.baseDAO.insert(info);
					}
					//必须设置为库中已经存在
					info.setInDatabase(true);
				} catch (Exception e) {
					this.logger.error("userQuestApp offline update error: roleId=" + role.getRoleId() + " questId=" + info.getQuestId());
				}
			}
		} catch (Exception ex) {
			Log4jManager.OFFLINE_ERROR_LOG.error("UserQuestApp error,roleId="
					+ role.getRoleId() + ",userId=" + role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void updateQuest(RoleInstance role) throws ServiceException {
		if(Util.isEmpty(role.getQuestTimeLimitSet())){
			return;
		}
		for(int questId : role.getQuestTimeLimitSet()){
			if(0 == questId){
				continue;
			}
			RoleQuestLogInfo info = role.getQuestLogMap().get(questId);
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
                logger.warn("system not exists quest,id=" + questId + " current role=" + role.getRoleId());
				continue ;
			}
			quest.update(role);
		}
	}
	
	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role,String npcId){
		if(null == role || Util.isEmpty(npcId)){
			return null ;
		}
		List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			List<GoodsOperateBean> fallList = quest.getQuestFall(role, npcId);
			if(Util.isEmpty(fallList)){
				continue;
			}
			goodsList.addAll(fallList);
		}	
		return goodsList;
	}
	
	@Override
	public void insertOrUpdateQuestLog(RoleInstance role, int questId, int phase, 
			int data1, int data2, int data3, QuestStatus status) throws ServiceException {
		this.insertOrUpdateUserQuestLog(role, questId, phase, data1, data2, data3, status,false);
	}
	
	private void insertOrUpdateUserQuestLog(RoleInstance role, int questId, int phase, 
			int data1, int data2, int data3, QuestStatus status,boolean whenAccept) throws ServiceException {
		//成功提交的逻辑在完成任务中处理，此处只更新任务日志
		if(QuestStatus.success == status){
			return;
		}
		//没有任务或未接取的不需要更新数据库
		if(QuestStatus.noneTask == status || QuestStatus.canAccept == status){
			return;
		}
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
		Quest quest = questApp.getQuest(questId);
		if(null == logInfo){
			logInfo = this.insertRoleQuestLog(role, questId, phase, data1, data2, data3, status);
			//限时任务
			if(quest.getTimeLimit() > 0){
				role.getQuestTimeLimitSet().add(questId);
			}
			return;
		}
		logInfo.setPhase(phase);
		logInfo.setData1(data1);
		logInfo.setData2(data2);
		logInfo.setData3(data3);
		logInfo.setStatus(status.getType());
		Date now = new Date();
		logInfo.setUpdateTime(now);
		if(whenAccept){
			//必须设置下面代码否则会出现限时任务第二天接到就失败
			logInfo.setCreateTime(now);
		}
		//限时任务
		if(quest.getTimeLimit() > 0){
			role.getQuestTimeLimitSet().add(questId);
		}
	}
	
	@Override
	public void updateQuestStatus(RoleInstance role, int questId, QuestStatus status) throws ServiceException {
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
		if(null == logInfo){
			this.insertRoleQuestLog(role, questId, 0, 0, 0, 0, status);
			return;
		}
		logInfo.setPhase(logInfo.getPhase());
		logInfo.setData1(logInfo.getData1());
		logInfo.setData2(logInfo.getData2());
		logInfo.setData3(logInfo.getData3());
		logInfo.setStatus(status.getType());
		logInfo.setUpdateTime(new Date());
	}
	
	@Override
	public void updateQuestLog(RoleInstance role, int questId, int index, OperatorType operator, int value) throws ServiceException{
		Quest quest = this.questApp.getQuest(questId);
		if(null == quest){
			return;
		}
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
		int data1=0,data2=0,data3=0;
		QuestStatus status = QuestStatus.notComplete;
		int phase=0;
		if(logInfo != null){
			data1 = logInfo.getData1();
			data2 = logInfo.getData2();
			data3 = logInfo.getData3();
			phase = logInfo.getPhase();
			status = QuestStatus.get(logInfo.getStatus());
		}
		switch(index){
		case 0:
			data1 = OperatorType.compute(data1, operator, value);
			break;
		case 1:
			data2 = OperatorType.compute(data2, operator, value);
			break;
		case 2:
			data3 = OperatorType.compute(data3, operator, value);
			break;
		default:
			throw new ServiceException("no has quest index! index="+index);
		}
		//没有任务日志
		if(null == logInfo){
			this.insertRoleQuestLog(role, questId, phase, data1, data2, data3, status);
			return;
		}
		//只修改缓存，不入库
		logInfo.setPhase(phase);
		logInfo.setData1(data1);
		logInfo.setData2(data2);
		logInfo.setData3(data3);
		logInfo.setStatus(status.getType());
		Date now = new Date();
		logInfo.setUpdateTime(now);
	}
	
	private RoleQuestLogInfo insertRoleQuestLog(RoleInstance role, int questId, int phase,
			int data1, int data2, int data3, QuestStatus status) throws ServiceException {
		try {
			RoleQuestLogInfo logInfo = new RoleQuestLogInfo();
			logInfo.setRoleId(role.getRoleId());
			logInfo.setQuestId(questId);
			logInfo.setData1(data1);
			logInfo.setData2(data2);
			logInfo.setData3(data3);
			logInfo.setPhase(phase);
			logInfo.setStatus(status.getType());
			Date now = new Date();
			logInfo.setCreateTime(now);
			logInfo.setUpdateTime(now);
			role.getQuestLogMap().put(questId, logInfo);
			return logInfo;
		} catch (Exception e) {
			throw new ServiceException("insertUserTaskLog" + e);
		}
	}
	
	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,NpcInstance npc) {
        List<NpcFunctionItem> retList = new ArrayList<NpcFunctionItem>();
        if(null == role || null == npc){
        	return retList ;
        }
        String npcId = npc.getNpc().getNpcid();
        List<NpcFunctionItem> notCompleteList = new ArrayList<NpcFunctionItem>();
        //判断可交的任务
		//在submitQuestSet并且在getQuestLogMap().keySet()中
		Set<Integer> submitQuestSet = questApp.getSubmitNpcMapping().get(npcId);
		if(!Util.isEmpty(submitQuestSet)){
			Set<Integer> notSubmit = Util.filterSet(submitQuestSet, role.getQuestLogMap().keySet(), false);
			for(Integer questId : notSubmit){
				//判断任务是否可提交
				Quest quest = questApp.getQuest(questId);
				if(null == quest){
					continue ;
				}
                if(quest.canSubmit(role)){
                    retList.add(new NpcFunctionItem(quest.getQuestName(),QuestHelper.QuestBeforeOperateReqCmdId,
						QuestHelper.formatQuestBeforeOperateParam(QuestStatus.canSubmit.getType(), quest.getQuestId())));
                }else{
                    notCompleteList.add(new NpcFunctionItem(quest.getQuestName(),QuestHelper.QuestBeforeOperateReqCmdId,
						QuestHelper.formatQuestBeforeOperateParam(QuestStatus.notComplete.getType(), quest.getQuestId())));
                }
			}
		}
		Set<Integer> acceptQuestSet = questApp.getAcceptNpcMapping().get(npcId);
		if(!Util.isEmpty(acceptQuestSet)){
			//判断是否符合接任务条件
			//排除已经接过的任务
			Set<Integer> notAccept = Util.filterSet(acceptQuestSet, role.getQuestLogMap().keySet(),true);
			for(Integer questId : notAccept){
				//判断是否能接此任务
				Quest quest = questApp.getQuest(questId);
				if(null == quest){
					continue ;
				}
				if(!quest.canAccept(role)){
					continue ;
				}
				retList.add(new NpcFunctionItem(quest.getQuestName(),QuestHelper.QuestBeforeOperateReqCmdId,
						QuestHelper.formatQuestBeforeOperateParam(QuestStatus.canAccept.getType(), quest.getQuestId())));
			}
		}
        //不可提交(未完成)
		if(notCompleteList.size() > 0){
            retList.addAll(notCompleteList);
            notCompleteList.clear();
        }
		return retList;
	}
	
	@Override
	public QuestOpCode debugAcceptQuest(RoleInstance role, int questId) throws OutOfGoodsBagException, ServiceException {
		Quest quest = questApp.getQuest(questId);
		if(null == quest){
			//任务不存在
			return QuestOpCode.questNotExists ;
		}
		try{
			this.acceptQuestWithoutCondition(role, quest);
		}catch(OutOfGoodsBagException ex){
			throw ex ;
		}catch(ServiceException ex){
			throw ex ;
		}
		return QuestOpCode.success;
	}

	@Override
	public QuestOpCode acceptQuest(RoleInstance role, int questId) throws OutOfGoodsBagException, ServiceException{
		Quest quest = questApp.getQuest(questId);
		if(null == quest){
			//任务不存在
			return QuestOpCode.questNotExists ;
		}
		if(!quest.canAccept(role)){
			//是否能接
			return QuestOpCode.canotAccept ;
		}
		//判断距离
		if(this.tooFar(role, quest, true)){
			return QuestOpCode.tooFar ;
		}
		try{
			this.acceptQuestWithoutCondition(role, quest);
		}catch(OutOfGoodsBagException ex){
			throw ex ;
		}catch(ServiceException ex){
			throw ex ;
		}
		//接任务触发剧情
		GameContext.getDramaApp().triggerDrama(role, DramaTriggerType.AcceptQuest
				, (short)0, null, questId, null);
		return QuestOpCode.success;
	}
	
    private Map<Integer,Integer> mergerGoodsMap(Quest quest/*,boolean isSubmitNotGiveup*/){
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(QuestPhase phase : quest.getPhaseList()){
			//附加物品
			Map<Integer,Integer> current = null ;
            current = phase.submitQuestGoodsMap();
			map = Util.mergerMap(map, current);
        }
         return map ;
    }

    private QuestOpCode submitQuestWithoutCondition(RoleInstance role, Quest quest) 
    	throws GoodsIsOnlyException,OutOfGoodsBagException,ServiceException{
    	Map<Integer,Integer> goodsMap = this.mergerGoodsMap(quest/*, true*/);
    	QuestAward questAward = quest.getQuestAward(role);
        //任务奖励
		if(null != questAward){
			try {
				Result result = questAward.execute(role, quest, goodsMap);
				if(!result.isSuccess()){
					return QuestOpCode.bagfullOrNoGoods ;
				}
			}catch (GoodsIsOnlyException e1) {
				throw e1 ;
			} catch (GoodsNotFoundException e) {
				throw e ;
			} catch (OutOfGoodsBagException e) {
				throw e ;
			} catch (ServiceException e) {
				throw e ;
			}
		} else {
			if (!Util.isEmpty(goodsMap)) {
				this.userGoodsApp.deleteSomeForBagByMap(role, goodsMap, OutputConsumeType.quest_submit_consume);
			}
		}
	   	try {
			this.completeQuestAction(role, quest);
		} catch (ServiceException e) {
		}
		int questId = quest.getQuestId();
		//交任务日志
		GameContext.getStatLogApp().roleTaskLog(role, 1, questId, questAward);
		//交任务触发剧情
		GameContext.getDramaApp().triggerDrama(role, DramaTriggerType.SubmitQuest
				, (short)0, null, questId, null);
		return QuestOpCode.success;
    }
    
    private void completeQuestAction(RoleInstance role,Quest quest) throws ServiceException {
    	int questId = quest.getQuestId();
    	//任务完成之后，要从任务日志表中删除
    	this.deleteRoleQuestLogInfo(role, questId);
    	QuestType questType = quest.getQuestType();
    	//主线和支线任务，保存任务完成的记录
    	if(QuestType.MainLine == questType){
    		role.setLastFinishQuestId(questId);
    		//完成主线、支线任务，打印日志
    		this.printMainBranchCompleteLog(role, quest);
    	}
    	//日常任务，保存任务完成的记录
    	if(QuestType.Daily == questType){
    		role.getQuestDailyFinished().completeQuest(questId);
    	}
		//完成任务时给角色增加Buff
		if(null != GameContext.getBuffApp().getBuff(quest.getCompleteBuff())){
			GameContext.getUserBuffApp().addBuffStat(role, role, quest.getCompleteBuff(), 1);
		}
		//主动push Npc头顶标识（传入任务对象，判断是否需要更新）
		this.notifyQuestNpcHeadSign(role, quest);
		this.refreshPoint(role, CollectPointNotifyType.CollectUnable, quest);
		
		//通知角色完成此任务
		role.getBehavior().sendMessage(new C0705_QuestSubmitRespMessage(questId));
		//在追踪面板主推下一个任务
		QuestHelper.pushNextQuestTrackMessage(role, quest.getNextQuestId());
		//目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.QuestFinish,
				String.valueOf(questId), 0);
    }
    
    /**
     * 完成主线、支线任务时，打印日志
     * @param role
     * @param quest
     */
    private void printMainBranchCompleteLog(RoleInstance role, Quest quest){
    	try {
    		StringBuffer buffer = new StringBuffer();
    		buffer.append(role.getRoleId()).append(Cat.pound).append(quest.getQuestId());
			Log4jManager.QUEST_MAIN_BRANCH_COMPLETE.info(buffer.toString());
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + " error: ", e);
		}
    }
    
	@Override
	public QuestOpCode submitQuest(RoleInstance role, int questId) throws GoodsIsOnlyException,
		GoodsNotFoundException,OutOfGoodsBagException,ServiceException{
		Quest quest = questApp.getQuest(questId);
		if(null == quest){
			//任务不存在
			return QuestOpCode.questNotExists ;
		}
		if(!quest.canSubmit(role)){
			//不能提交
			return QuestOpCode.cannotSubmit ;
		}
		//判断距离
		if(this.tooFar(role, quest, false)){
			return QuestOpCode.tooFar ;
		}
		return this.submitQuestWithoutCondition(role, quest);
	}
	
	private boolean tooFar(RoleInstance role, Quest quest, boolean isAccept) {
		try {
			if(!GameContext.getParasConfig().isCheckQuestDistance()){
				return false ;
			}
			if (null == quest.getQuestAcceptType()
					|| !quest.getQuestAcceptType().isDistance()) {
				// 不需要判断距离
				return false;
			}
			List<QuestPhase> phaseList = quest.getPhaseList();
			if (Util.isEmpty(phaseList)) {
				return false;
			}
			QuestPhase phase = null;
			if (isAccept) {
				phase = phaseList.get(0);
			} else {
				phase = phaseList.get(phaseList.size() - 1);
			}
			if (null == phase) {
				return false;
			}
			if (Util.isEmpty(phase.getMapId())) {
				return false;
			}
			if (Util.isEmpty(role.getMapId())
					|| !role.getMapId().equals(phase.getMapId())) {
				// 没在同一地图
				return true;
			}
			
			//只判断是否在同一地图，因为地图内有内部跳转点，有几率导致判断错误
			/*Point p = phase.getEventPoint(role);
			if(null == p){
				return false ;
			}
			return Util.distance(role.getMapX(), role.getMapY(), p
					.getX(), p.getY()) > MapConstant.QUEST_MAX_DISTANCE;*/
					
			return false ;
		}catch(Exception ex){
			logger.error("",ex);
		}
		return false ;
	}
	
	@Override
	public QuestOpCode submitQuestNoCondition(RoleInstance role,int questId) throws ServiceException{
		Quest quest = questApp.getQuest(questId);
		if(null == quest){
			//任务不存在
			return QuestOpCode.questNotExists ;
		}
		return this.submitQuestWithoutCondition(role, quest);
	}

    @Override
	public int giveUpQuest(RoleInstance role, int questId) throws ServiceException {
        //只能放弃正在做的任务
    	if(!role.hasReceiveQuestNow(questId)){
        	return 0;
        }
        Quest quest = questApp.getQuest(questId);
        if(null != quest){
        	quest.giveUp(role);
        }
        //删除任务日志
        this.deleteRoleQuestLogInfo(role, questId);
        //刷新NPC头顶
		this.notifyQuestNpcHeadSign(role);
		//刷新采集点
		this.refreshPoint(role, CollectPointNotifyType.CollectUnable, quest);
		//通知角色放弃此任务成功
		role.getBehavior().sendMessage(new C0706_QuestGiveupRespMessage(questId));
		//放弃任务日志
		GameContext.getStatLogApp().roleTaskLog(role, 2, questId, null);
		return 1 ;
	}
    
    /**
	 * 删除任务日志
	 * @param roleId
	 * @param questId
	 * @throws ServiceException
	 */
	private void deleteRoleQuestLogInfo(RoleInstance role, int questId) throws ServiceException {
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
		if(null == logInfo){
			return;
		}
		//只有数据库中存在的日志，才进行删除
		if(logInfo.isInDatabase()){
			this.baseDAO.delete(RoleQuestLogInfo.class, "roleId", role.getRoleId() ,"questId", String.valueOf(questId));
		}
		role.getQuestLogMap().remove(questId);
		role.getQuestTimeLimitSet().remove(questId);
	}

	@Override
	public void notifyQuestNpcHeadSign(RoleInstance role) {
		try {
			if (null == role || null == role.getMapInstance()) {
				return;
			}
			MapInstance mapInstance = role.getMapInstance();
			Collection<NpcInstance> npcList = mapInstance.getNpcList();
			if (Util.isEmpty(npcList)) {
				return;
			}
			List<NpcHeadSignItem> items = new ArrayList<NpcHeadSignItem>();
			for (NpcInstance current : npcList) {
				if(!GameContext.getQuestApp().isQuestNpc(current)){
					continue ;
				}
				items.add(new NpcHeadSignItem(current.getIntRoleId(), this
						.getNpcQuestHeadSign(role, current)));
			}
			C0700_NpcHeadSignRespMessage respMsg = new C0700_NpcHeadSignRespMessage();
			respMsg.setItems(items);
			this.messageCenter.send("", role.getUserId(), respMsg);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	private byte getNpcQuestHeadSign(RoleInstance role, String npcId, int npcForce){
		Set<Integer> thisNpcAccept = questApp.getAcceptNpcMapping().get(npcId);
		Set<Integer> thisNpcSubmit = questApp.getSubmitNpcMapping().get(npcId);
		if(null == thisNpcAccept && null == thisNpcSubmit){
			return NpcQuestHeadSign.None.getType();
		}
        boolean haveCanotSubmit = false ;
		//优先交任务
		if(null != thisNpcSubmit){
			Set<Integer> thisRoleNotSubmit = role.getQuestLogMap().keySet();
			//计算thisNpcSubmit,thisRoleNotSubmit的交集
			Set<Integer> result = Util.filterSet(thisNpcSubmit, thisRoleNotSubmit, false);
			for(int questId : result){
				Quest quest = questApp.getQuest(questId);
				if(null == quest){
					continue ;
				}
				if(quest.canSubmit(role)){
					return NpcQuestHeadSign.Submit.getType(); //问号
				}
                haveCanotSubmit = true ;
			}
		}
		//判断接任务
		if(null != thisNpcAccept){
            for(int questId : thisNpcAccept){
                Quest quest = questApp.getQuest(questId);
				if(null == quest){
					continue ;
				}
                if(role.hasReceiveQuestNow(questId)){
                    //目前未完成任务
                    continue ;
                }
                if(quest.canAccept(role)){
					return NpcQuestHeadSign.Accept.getType();//叹号
				}
            }
		}
        if(haveCanotSubmit){
            return NpcQuestHeadSign.notComplete.getType(); //灰色问号
        }
		return NpcQuestHeadSign.None.getType();
	}
	
	/**
	 * 任务变化通知NPC头顶标识
	 * 只有主线、支线任务需要通知NPC头顶标识
	 * @param role 角色
	 * @param quest 任务
	 */
	private void notifyQuestNpcHeadSign(RoleInstance role, Quest quest){
		try {
			QuestType questType = quest.getQuestType();
			//加载到NPC身上的任务才可能通知NPC头顶标识
			if(!questType.isPutNpc()){
				return;
			}
			//从NPC身上接取的才能通知NPC头顶标识
			if(QuestAcceptType.Npc != quest.getQuestAcceptType()){
				return;
			}
			this.notifyQuestNpcHeadSign(role);
		} catch (RuntimeException e) {
			this.logger.error("UserQuestApp.notifyQuestNpcHeadSign error: ", e);
		}
	}
	
	@Override
	public byte getNpcQuestHeadSign(RoleInstance role,NpcInstance npc){
		if(!NpcType.questEnable(npc.getNpc().getNpctype())){
			return NpcQuestHeadSign.None.getType();
		}
		return getNpcQuestHeadSign(role,npc.getNpcid(),0);
	}
	
	@Override
	public byte getNpcQuestHeadSign(RoleInstance role,NpcTemplate npcTemp){
		if(!NpcType.questEnable(npcTemp.getNpctype())){
			return NpcQuestHeadSign.None.getType();
		}
		return getNpcQuestHeadSign(role,npcTemp.getNpcid(),0);
	}

	
	@Override
	public void notifyQuestNpcHeadSign(NpcInstance npc) {
		if(null == npc || null == npc.getMapInstance()){
			return ;
		}
		if(!GameContext.getQuestApp().isQuestNpc(npc)){
			return ;
		}
		MapInstance mapInstance = npc.getMapInstance();
		Collection<RoleInstance> roleList = mapInstance.getRoleList();
		if (Util.isEmpty(roleList)) {
			return;
		}
		for (RoleInstance role : roleList) {
			byte npcQusetHeadSign = this.getNpcQuestHeadSign(role, npc);
			if(npcQusetHeadSign == NpcQuestHeadSign.None.getType()) {
				continue;
			}
			List<NpcHeadSignItem> items = new ArrayList<NpcHeadSignItem>();
			items.add(new NpcHeadSignItem(npc.getIntRoleId(), npcQusetHeadSign));
			C0700_NpcHeadSignRespMessage respMsg = new C0700_NpcHeadSignRespMessage();
			respMsg.setItems(items);
			this.messageCenter.send("", role.getUserId(), respMsg);
		}
	}

	public void setQuestApp(QuestApp questApp) {
		this.questApp = questApp;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	public void setUserGoodsApp(UserGoodsApp userGoodsApp) {
		this.userGoodsApp = userGoodsApp;
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		this.messageCenter = messageCenter;
	}
	
	@Override
	public int isCanQuestTrigger(RoleInstance role, GoodsTaskprops goods){
		int questId = goods.getTaskId();
		// 任务不存在
		Quest quest = questApp.getQuest(questId);
		if (null == quest) {
			return 2;
		}
		// 判断用户是否可以接受此任务
		if (!quest.canAccept(role)) {
			return 3;
		}
		return 1 ;
	}

	@Override
	public int questTrigger(RoleInstance role, GoodsTaskprops goods) {
		int questId = goods.getTaskId();
		int isCan = this.isCanQuestTrigger(role, goods);
		if(1 != isCan){
			return isCan ;
		}
		Quest quest = questApp.getQuest(questId);
		// 给用户push触发的任务信息
		this.messageCenter.send("", role.getUserId(), QuestHelper
				.questBeforeOperateRespMessageBuilder4GoodsTigger(role, quest,
						goods));
		return 1;
	}

	@Override
	public void acceptQuestWithoutCondition(RoleInstance role, int questId) throws ServiceException{
		Quest quest = questApp.getQuest(questId);
		acceptQuestWithoutCondition(role,quest);
	}

     @Override
    public void acceptAndCompleteQuest(RoleInstance role, int questId) throws ServiceException {
        Quest quest = questApp.getQuest(questId);
        if(null == quest){
            return ;
        }
        if(role.getQuestLogMap().containsKey(questId)){
            //当前已经接到此任务
            this.submitQuestWithoutCondition(role, quest);
            return ;
        }
        if(!quest.canAccept(role)){
            return ;
        }
        try {
			this.completeQuestAction(role, quest);
		} catch (ServiceException e) {
			throw e ;
		}
    }
	
	private void acceptQuestWithoutCondition(RoleInstance role, Quest quest) throws OutOfGoodsBagException, ServiceException{
		if(null == quest){
			return ;
		}
		//接任务
		int currentPhase = 0 ;
		QuestStatus status = QuestStatus.notComplete ;
		QuestPhase firstPhase = quest.getPhaseList().get(0);
		if(firstPhase.isPhaseComplete(role)){
			//第一阶段为接任务阶段
			currentPhase = 1 ;
		}
		if(currentPhase == 1){
			if (firstPhase.completePhaseAction(role)<=0){
				//背包已经满
				throw new OutOfGoodsBagException(GameContext.getI18n().getText(TextId.Quest_Backpack_Full));
			}
			QuestPhase secondPhase = quest.getPhaseList().get(1);
			//下面不能调用 secondPhase.isPhaseComplete(role),因为此时还没有相关日志会报空指针
			if(QuestHelper.isPhaseTermsComplete(role, secondPhase)){
					/*QuestHelper.allGoodsTermsAndComplete(role,secondPhase)
					|| QuestHelper.attributeTermsAndComplete(role,secondPhase)){*/
				//收集物品类型,有可能背包里面有此类型物品，已经满足交任务条件，所以需要判断下
				currentPhase ++ ;
				status = QuestStatus.canSubmit;
			}
		}
		int questId = quest.getQuestId();
		this.insertOrUpdateUserQuestLog(role, questId, currentPhase, 0, 0, 0, status,true);
		//主动push Npc头顶标识（传入任务对象，判断是否需要更新）
		this.notifyQuestNpcHeadSign(role, quest); 
		//刷新采集点
		this.refreshPoint(role, CollectPointNotifyType.CollectAble, quest);
		
		//接任务成功，更新任务追踪信息
		QuestHelper.pushQuestTrackUpdateNotifyMessage(role, quest);
		//接任务日志
		GameContext.getStatLogApp().roleTaskLog(role, 0, questId, null);
	}

	@Override
	public void removeAllQuest(RoleInstance role) throws ServiceException {
		role.getQuestLogMap().clear();
		this.baseDAO.delete(RoleQuestLogInfo.class, "roleId", role.getRoleId());
		notifyQuestNpcHeadSign(role);
	}
	
	@Override
	public void enterMap(RoleInstance role) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(quest == null){
				continue ;
			}
			quest.enterMap(role); 	
		}
	}
	
	@Override
	public void pickupGoods(RoleInstance role,int goodsId,int goodsNum) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.getGoods(role, goodsId,goodsNum);
		}		
	}
	
	/**
	 * 触发属性改变
	 * @param role
	 * @param type
	 * @param num
	 * @throws ServiceException
	 */
	public void changeAttriForQuest(RoleInstance role,int type) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.getAttribute(role, type);
		}		
	}
	
	@Override
	public void useGoods(RoleInstance role,int goodsId) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.useGoods(role, goodsId);
		}		
	}
    
	@Override
	public void triggerEvent(RoleInstance role, String eventId)
			throws ServiceException {
		 Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
			while(it.hasNext()){
				RoleQuestLogInfo info = it.next();
				if(info.getStatus() == QuestStatus.failure.getType()){
					//已经失败的不需要更新
					continue ;
				}
				int questId = info.getQuestId();
				Quest quest = questApp.getQuest(questId);
				if(null == quest){
					continue ;
				}
				quest.triggerEvent(role, eventId);
				//队伍不共享
			}
	}
	
	/**
	 * 获取可共享任务进度的角色列表
	 * 同一地图内的队友和门派在线帮众
	 * @param role
	 * @return
	 */
	private Map<Integer, RoleInstance> getQuestShareRoles(AbstractRole role,NpcInstance npcInstance){
		Map<Integer, RoleInstance> shareRoleMap = new HashMap<Integer, RoleInstance>();
		MapInstance mapInstance = role.getMapInstance();
		if(2 == npcInstance.getNpc().getQuestHatred()){
			//当前地图都可完成任务
			if(null == mapInstance){
				return shareRoleMap ;
			}
			//当前地图的玩家全部完成
			for (RoleInstance itemRole : mapInstance.getRoleList()) {
				if (role.getRoleId().equals(itemRole.getRoleId())) {
					// 将role排除
					continue;
				}
				shareRoleMap.put(itemRole.getIntRoleId(), itemRole);
			}
			return shareRoleMap;
		}
		// 是否NPC仇恨列表中的玩家都可完成任务
		boolean hatredShared = npcInstance.getNpc().getQuestHatred() == 1 ;
		HatredTarget ht = npcInstance.getHatredTarget();
		if (hatredShared && null != ht) {
			try {
				if (null != mapInstance) {
					SortedValueNumMap hMap = ht.getHatredMap();
					for (Object haredRoleId : hMap.keySet()) {
						String roleId = haredRoleId.toString();
						if(role.getRoleId().equals(roleId)){
							//将role排除
							continue ;
						}
						RoleInstance itemRole = mapInstance.getRoleInstance(roleId);
						if (null == itemRole) {
							continue;
						}
						shareRoleMap.put(itemRole.getIntRoleId(), itemRole);
					}
				}
			} catch (Exception ex) {
			}
		}
		if(role.getRoleType() != RoleType.PLAYER){
			return shareRoleMap ;
		}
		//同地图队伍内共享
		RoleInstance player = (RoleInstance)role;
		Team team = player.getTeam();
		if(null != team){
			this.addRoleToShareMap(player, shareRoleMap, team.getMembers());
		}
		return shareRoleMap ;
	}
	
	/**
	 * 可共享任务进度的角色
	 * @param role
	 * @param shareRoleMap
	 * @param collection
	 * @return 在同一张地图的队友和帮众（不包括自己）
	 */
	private void addRoleToShareMap(RoleInstance role, Map<Integer, RoleInstance> shareRoleMap, 
			Collection<? extends AbstractRole> collection){
		for(AbstractRole member : collection){
			if(null == member){
				continue;
			}
			int memberId = member.getIntRoleId();
			//不包括自己
			if(memberId == role.getIntRoleId()){
				continue;
			}
			//过滤重复
			if(shareRoleMap.containsKey(memberId)){
				continue;
			}
			RoleInstance thatRole = (RoleInstance) member;
			//判断是否在同一张地图内
			if(!this.inSameMap(role, thatRole)){
				continue ;
			}
			shareRoleMap.put(memberId,thatRole );
		}
	}
	
	private boolean inSameMap(AbstractRole thisRole,AbstractRole thatRole){
		if(null == thisRole || null == thatRole){
			return false ;
		}
		MapInstance thisMap = thisRole.getMapInstance() ;
		MapInstance thatMap = thatRole.getMapInstance() ;
		if(null == thisMap || null == thatMap){
			return false ;
		}
		return thisMap.getInstanceId().equals(thatMap.getInstanceId());
	}
	
	@Override
	public void killMonster(AbstractRole owner, NpcInstance npcInstance) throws ServiceException {
		String npcId = npcInstance.getNpc().getNpcid() ;
		//自己的任务
		this.selfKillMonster(owner, npcId);
		//队伍和门派内需要共享任务进度
		Map<Integer, RoleInstance> shareRoles = this.getQuestShareRoles(owner,npcInstance);
		if(!Util.isEmpty(shareRoles)){
			for(RoleInstance current : shareRoles.values()){
				if(current.getIntRoleId() == owner.getIntRoleId()){
					continue ;
				}
				this.selfKillMonster(current, npcId);
			}
		}
	}
	
	private void selfKillMonster(AbstractRole owner, String npcId){
		if(owner.getRoleType() != RoleType.PLAYER){
			return ;
		}
		RoleInstance role = (RoleInstance)owner ;
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.killMonster(role, npcId);
		}
	}
	
	@Override
	public void killRole(RoleInstance attacker, RoleInstance victim) throws ServiceException {
		//自己的任务
		this.selfKillRole(attacker, victim);
		//队伍共享
		Team team = attacker.getTeam();
		if(null == team){
			return ;
		}
		for(AbstractRole member : team.getMembers()){
			if(member.getIntRoleId() == attacker.getIntRoleId()){
				continue ;
			}
			if(!this.inSameMap(attacker, member)){
				continue ;
			}
			this.selfKillRole((RoleInstance)member, victim);
		}
	}
	
	private void selfKillRole(RoleInstance attacker, RoleInstance victim){
		Iterator<RoleQuestLogInfo> it = attacker.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.killRole(attacker);
		}
	}
	
	@Override
	public void death(RoleInstance role) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.death(role);
		}		
	}
	
	@Override
	public void discardAttributeNotify(String roleId) {
		if (null == roleId || 0 == roleId.trim().length()) {
			return;
		}
		RoleInstance role = GameContext.getOnlineCenter()
				.getRoleInstanceByRoleId(roleId);
		if (null == role) {
			return;
		}
		int notify = 0 ;
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values()
				.iterator();
		while (it.hasNext()) {
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			notify += quest.discardAttributeNotify(role);
		}
		if(notify > 0){
			//刷新NPC头顶标识
			notifyQuestNpcHeadSign(role);
		}
	}
	
	@Override
	public void discardGoodsNotify(String roleId, int goodsTemplateId,
			int discardCount) {
		if (null == roleId || 0 == roleId.trim().length()
				|| goodsTemplateId <= 0 || discardCount <= 0) {
			return;
		}
		RoleInstance role = GameContext.getOnlineCenter()
				.getRoleInstanceByRoleId(roleId);
		if (null == role) {
			return;
		}
		int notify = 0 ;
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values()
				.iterator();
		while (it.hasNext()) {
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			notify += quest.discardGoodsNotify(role, goodsTemplateId);
		}
		if(notify > 0){
			//刷新NPC头顶标识
			notifyQuestNpcHeadSign(role);
			//如果丢弃的是任务物品,则还需要刷新任务采集点信息
			try {
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsTemplateId);
				if(gb instanceof GoodsTask){
					//TODO:提高性能只刷新与采集点相关的任务
					//刷新采集点
					GameContext.getUserMapApp().pushCollectPointMessage(role);
				}
			} catch (Exception e) {
				this.logger.error("", e);
			} 
		}
	}

	/**
	 * 任务变化刷新采集点
	 * 是采集类型的任务才刷新
	 * @param role
	 * @param pointType
	 * @param quest
	 */
	public void refreshPoint(RoleInstance role, CollectPointNotifyType pointType, Quest quest) {
		try {
			//是否需要刷新采集点（只有采集类型的任务才刷新）
			boolean needRefresh = false;
			List<QuestTerm> termList = QuestHelper.getQuestTermList(quest);
			for(QuestTerm term : termList){
				QuestTermType termType = term.getQuestTermType();
				if(QuestTermType.CollectPoint == termType || QuestTermType.TriggerEvent == termType){
					needRefresh = true;
					break;
				}
			}
			//不需要刷新，直接返回
			if(!needRefresh){
				return;
			}
			MapInstance mapInstance = role.getMapInstance();
			if (null == mapInstance) {
				return;
			}
			java.util.Map<String, CollectablePoint<RoleInstance>> pointMap = mapInstance.getCollectPointMap();
			if (null == pointMap || 0 == pointMap.size()) {
				return;
			}
			CollectPointIdItem itemList = new CollectPointIdItem();
			int questId = quest.getQuestId();
			for (String key : pointMap.keySet()) {
				CollectablePoint<RoleInstance> point = pointMap.get(key);
				CollectPoint cp = null ;
				if(null == point || (null == (cp = point.getCollectPoint()))){
					return ;
				}
				if(PointType.QuestCollectPoint.getType() != cp.getType()){
					continue ;
				}
				if (cp.getMasterId() != questId) {
					continue;
				}
				CollectPointIdItem2 item = new CollectPointIdItem2();
				item.setInstanceIds(point.getInstanceId());
				item.setType(pointType == CollectPointNotifyType.CollectUnable ? (byte) 0 : (byte) 1);
				item.setCollectType((byte) PointType.QuestCollectPoint.getType());
				item.setDisplayFlag((byte) 0);
				itemList.getList().add(item);
			}
			if(null == itemList.getList() || 0 == itemList.getList().size()){
				return ;
			}
			C0606_CollectPointNotifyMessage notify = new C0606_CollectPointNotifyMessage();
			notify.setType((byte) pointType.getType());
			notify.setItem(itemList);
			this.messageCenter.send("", role.getUserId(), notify);
		} catch (Exception e) {
			this.logger.error("", e);
		}
	}
	
	@Override
	public void roleUpgrade(RoleInstance role) {
		//this.notifyQuestNpcHeadSign(role);
		try {
			changeAttriForQuest(role,AttributeType.level.getType());
		} catch (Exception e) {
		}
	}

	@Override
	public void chooseMenu(RoleInstance role, int menuId) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.choseMenu(role, menuId);
		}
	}

	@Override
	public Map<QuestType,List<QuestTempItem>> getReceiveQuest(RoleInstance role) {
		Map<QuestType, List<QuestTempItem>> questMap = new HashMap<QuestType, List<QuestTempItem>>();
		for(RoleQuestLogInfo questLogInfo : role.getQuestLogMap().values()){
			if(null == questLogInfo){
				continue;
			}
			Quest quest = GameContext.getQuestApp().getQuest(questLogInfo.getQuestId());
			if(null == quest){
				continue;
			}
			QuestType questType = quest.getQuestType();
			if(!questMap.containsKey(questType)){
				questMap.put(questType, new ArrayList<QuestTempItem>());
			}
			QuestTempItem item = new QuestTempItem();
			item.setQuestId(quest.getQuestId());
			item.setQuestName(quest.getQuestName());
			item.setQuestType(questType);
			QuestStatus questStatus = QuestHelper.getQuestStatus(role, quest, questLogInfo);
			item.setQuestStatus(questStatus);
			item.setMinLevel(quest.getMinLevel());
			questMap.get(questType).add(item);
		}
		return questMap;
	}
	
	@Override
	public Map<QuestType, List<QuestTempItem>> getCanAcceptQuest(RoleInstance role) {
		Map<QuestType, List<QuestTempItem>> questMap = new HashMap<QuestType, List<QuestTempItem>>();
		for(int questId : questApp.getNpcAcceptQuestList()){
			Quest quest = this.questApp.getQuest(questId);
			if(null == quest){
				continue;
			}
			QuestType questType = quest.getQuestType();
			//是否在可接列表中显示（只有在NPC山上接取的，才能列出来）
			if(!questType.isPutNpc()){
				continue;
			}
			if(!quest.canAccept(role)){
				continue;
			}
			//道具触发的任务，需要判断背包是否有道具
			int goodsId = quest.getRelyGoodsId();
			if(0 != goodsId && !role.getRoleBackpack().existGoods(goodsId)){
				continue;
			}
			if(!questMap.containsKey(questType)){
				questMap.put(questType, new ArrayList<QuestTempItem>());
			}
			QuestTempItem item = new QuestTempItem();
			item.setQuestId(questId);
			item.setQuestName(quest.getQuestName());
			item.setQuestType(questType);
			item.setQuestStatus(QuestStatus.canAccept);
			item.setMinLevel(quest.getMinLevel());
			questMap.get(questType).add(item);
		}
		return questMap;
	}
	
	@Override
	public C0701_QuestListRespMessage getQuestListRespMessage(RoleInstance role, QuestListReqestType reqestType){
		Map<QuestType,List<QuestTempItem>> questTempMap = null;
		//返回任务列表的类型
		byte type = reqestType.getType();
		switch(reqestType){
		case Received:
			questTempMap = this.getReceiveQuest(role);
			break;
		case CanAccept:
			questTempMap = this.getCanAcceptQuest(role);
			break;
		case Auto:
			//优先给已接任务
			type = QuestListReqestType.Received.getType();
			questTempMap = this.getReceiveQuest(role);
			//如果身上没有任务，再给可接任务
			if(Util.isEmpty(questTempMap)){
				type = QuestListReqestType.CanAccept.getType();
				questTempMap = this.getCanAcceptQuest(role);
			}
			break;
		}
		C0701_QuestListRespMessage resp = new C0701_QuestListRespMessage();
		//请求的type=2时，会根据具体情况设置为0或1
		resp.setType(type);
		if(Util.isEmpty(questTempMap)){
			return resp;
		}
		List<QuestGroupItem> groupList = new ArrayList<QuestGroupItem>();
		for(QuestType questType : QuestType.values()){
			if(!questTempMap.containsKey(questType)){
				continue;
			}
			List<QuestTempItem> questTempList = questTempMap.get(questType);
			//任务列表排序
			this.sortQuestSimpleInfoList(questTempList);
			List<QuestSimpleInfoItem> simpleInfoList = new ArrayList<QuestSimpleInfoItem>();
			for(QuestTempItem temp : questTempList){
				if(null == temp){
					continue;
				}
				QuestSimpleInfoItem simpleInfoItem = new QuestSimpleInfoItem();
				simpleInfoItem.setQuestId(temp.getQuestId());
				simpleInfoItem.setName(temp.getQuestName());
				simpleInfoItem.setStatus((byte) temp.getQuestStatus().getType());
				simpleInfoList.add(simpleInfoItem);
			}
			QuestGroupItem groupItem = new QuestGroupItem();
			groupItem.setGroupType((byte) questType.getType());
			groupItem.setGroupName(questType.getName());
			groupItem.setSimpleInfoList(simpleInfoList);
			groupList.add(groupItem);
		}
		resp.setGroupList(groupList);
		return resp;
	}
	
	/**
	 * 根据任务状态进行排序
	 * 可提交 > 未完成 > 失败
	 * @param simpleInfoList
	 */
	private void sortQuestSimpleInfoList(List<QuestTempItem> questTempList){
		Collections.sort(questTempList, new Comparator<QuestTempItem>(){
			@Override
			public int compare(QuestTempItem item1, QuestTempItem item2) {
				if(item1.getQuestStatus() == item2.getQuestStatus()){
					if(item1.getMinLevel() > item2.getMinLevel()){
						return 1;
					}
					return -1;
				}
				//首先是可提交的
				if(QuestStatus.canSubmit == item1.getQuestStatus()){
					return -1;
				}
				if(QuestStatus.canSubmit == item2.getQuestStatus()){
					return 1;
				}
				//其次是失败的
				if(QuestStatus.failure == item1.getQuestStatus()){
					return -1;
				}
				if(QuestStatus.failure == item2.getQuestStatus()){
					return 1;
				}
				//最后是未完成的
				if(QuestStatus.notComplete == item1.getQuestStatus()){
					return -1;
				}
				return 1;
			}
		});
	}

	@Override
	public void pushQuestViewMessage(RoleInstance role, String npcId) {
		try {
			if(Util.isEmpty(npcId)){
				return;
			}
			//优先可提交的任务
			Set<Integer> submitQuestSet = questApp.getSubmitNpcMapping().get(npcId);
			if(!Util.isEmpty(submitQuestSet)){
				for(int questId : submitQuestSet){
					Quest quest = questApp.getQuest(questId);
					if(null == quest){
						continue ;
					}
	                if(!quest.canSubmit(role)){
						continue;
	                }
	                //弹出任务信息面板
	                this.pushQuestViewMessage(role, QuestStatus.canSubmit, questId);
	                return;
				}
			}
			//可接取的任务
			Set<Integer> acceptQuestSet = questApp.getAcceptNpcMapping().get(npcId);
			if(!Util.isEmpty(acceptQuestSet)){
				List<Integer> list = new ArrayList<Integer>();
				for(int questId : acceptQuestSet){
					Quest quest = questApp.getQuest(questId);
					if(null == quest){
						continue;
					}
					if(!quest.canAccept(role)){
						continue;
	                }
					//如果是主线任务，直接弹出
					if(QuestType.MainLine == quest.getQuestType()){
						//弹出任务信息面板
						this.pushQuestViewMessage(role, QuestStatus.canAccept, questId);
						return;
					}
					list.add(questId);
				}
				//如果不没有可接取的主线任务，弹出一个可接取的支线任务
				if(list.size() > 0){
					//弹出任务信息面板
					this.pushQuestViewMessage(role, QuestStatus.canAccept, list.get(0));
				}
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".pushQuestViewMessage error: ", e);
		}
	}
	
	/**
	 * 主推任务详情面板
	 * @param role 角色
	 * @param status 任务状态
	 * @param questId 任务ID
	 */
	private void pushQuestViewMessage(RoleInstance role, QuestStatus status, int questId){
		try{
			if(null == role || null == status || questId < 0){
				return;
			}
			String param = QuestHelper.formatQuestBeforeOperateParam(status.getType(), questId);
			GameContext.getMessageCenter().sendSysMsg(role, QuestHelper.questBeforeOperateRespMessageBuilder(role, param));
		}catch(Exception e) {
			this.logger.error(this.getClass().getName() + ".pushQuestViewMessage error: ", e);
		}
	}
	
	/*@Override
	public void pushNextQuestViewMessage(RoleInstance role){
		try {
			int lastQuestId = role.getLastFinishQuestId();
			Quest lastQuest = this.questApp.getQuest(lastQuestId);
			if(null == lastQuest){
				return;
			}
			int questId = lastQuest.getNextQuestId();
			Quest quest = this.questApp.getQuest(questId);
			if(null == quest){
				return;
			}
			//正在做这个任务，不弹出面板
			if(role.hasReceiveQuestNow(questId)){
				return;
			}
			QuestStatus status = QuestHelper.getQuestStatus(role, quest);
			this.pushQuestViewMessage(role, status, questId);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".pushNextQuestViewMessage error: ", e);
		}
	}*/

	/*@Override
	public void acceptNpcAllQuest(RoleInstance role, String npcId) {
		try{
			Set<Integer> acceptQuestSet = questApp.getAcceptNpcMapping().get(npcId);
			if(!Util.isEmpty(acceptQuestSet)){
				for(int questId : acceptQuestSet){
					Quest quest = questApp.getQuest(questId);
					if(null == quest){
						continue;
					}
					if(!quest.canAccept(role)){
						continue;
	                }
					//接取任务
					try {
						this.acceptQuest(role, questId);
					} catch (OutOfGoodsBagException e) {
						e.printStackTrace();
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e) {
			this.logger.error("UserQuestApp.acceptNpcAllQuest error: ", e);
		}
	}*/

	@Override
	public void copyMapPass(RoleInstance role, String mapId) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.copyMapPass(role, mapId);
		}
	}

	@Override
	public void mapRefreshNpc(RoleInstance role, int refreshIndex) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.mapRefreshNpc(role, refreshIndex);
		}
	}

	@Override
	public void copyMapPass(RoleInstance role, short copyId) throws ServiceException {
		Iterator<RoleQuestLogInfo> it = role.getQuestLogMap().values().iterator();
		while(it.hasNext()){
			RoleQuestLogInfo info = it.next();
			if(info.getStatus() == QuestStatus.failure.getType()){
				//已经失败的不需要更新
				continue ;
			}
			int questId = info.getQuestId();
			Quest quest = questApp.getQuest(questId);
			if(null == quest){
				continue ;
			}
			quest.copyPass(role, copyId);
		}
	}
	
}
