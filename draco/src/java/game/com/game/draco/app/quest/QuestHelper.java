package com.game.draco.app.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.phase.AcceptQuestPhase;
import com.game.draco.app.quest.phase.SubmitQuestPhase;
import com.game.draco.message.item.QuestInfoItem;
import com.game.draco.message.item.QuestTermItem;
import com.game.draco.message.item.QuestTrackItem;
import com.game.draco.message.push.C0709_QuestTrackUpdateNotifyMessage;
import com.game.draco.message.push.C0710_QuestTrackTermNotifyMessage;
import com.game.draco.message.push.C0711_QuestTrackStatusNotifyMessage;
import com.game.draco.message.request.C0703_QuestBeforeOperateReqMessage;
import com.game.draco.message.response.C0703_QuestBeforeOperateRespMessage;

public class QuestHelper {
	
	public static final short QuestBeforeOperateReqCmdId = new C0703_QuestBeforeOperateReqMessage().getCommandId();
	public static final String QuestId_Key = "questid";
	public static final String QuestStatus_Key = "status";
	public static final String QuestAwardGoodsId_key = "goodsid";
	public static final String QuestGoodsTriggerGoodsId_Key = "goodsid";
	public static final String QuestSharedRoleId_Key = "roleid";
	public static final String QuestSharedCancel_Key = "cancel";
	private static final int ONE_MINUTE = 60*1000;
	private final static Logger logger = LoggerFactory.getLogger(QuestHelper.class);
	
	/**
	 * 拼接 接交任务前提示页面的请求参数
	 * (即NPC功能返回里面任务部分)
	 * @param status
	 * @param questId
	 * @return
	 */
	public static String formatQuestBeforeOperateParam(int status,int questId){
		return QuestStatus_Key + Cat.equ + status + Cat.and + QuestId_Key + Cat.equ +  questId ;
	}
	
	/**
	 * 任务追踪面板更新（接任务成功、增加一个追踪信息）
	 * @param role
	 * @param quest
	 */
	public static void pushQuestTrackUpdateNotifyMessage(RoleInstance role,Quest quest){
		try {
			//接任务成功，更新任务追踪信息
			C0709_QuestTrackUpdateNotifyMessage trackNotifyMsg = new C0709_QuestTrackUpdateNotifyMessage();
			trackNotifyMsg.setQuestTrackItem(QuestHelper.getQuestTrackInfo(role, quest));
			GameContext.getMessageCenter().send("", role.getUserId(), trackNotifyMsg);
		} catch (Exception e) {
			logger.error(QuestHelper.class.getName() + ".pushQuestTrackUpdateNotifyMessage error: ", e);
		}
	}
	
	public static void pushNextQuestTrackMessage(RoleInstance role, int nextQuestId){
		try {
			if(nextQuestId <= 0){
				return;
			}
			Quest quest = GameContext.getQuestApp().getQuest(nextQuestId);
			if(null == quest){
				return;
			}
			QuestHelper.pushQuestTrackUpdateNotifyMessage(role, quest);
		} catch (Exception e) {
			logger.error(QuestHelper.class.getName() + ".pushNextQuestTrackMessage error: ", e);
		}
	}
	
	/**
	 * 任务完成进度（任务追踪更新）
	 * @param role
	 * @param quest
	 */
	public static void pushQuestCanSubmitTipMessage(RoleInstance role,Quest quest){
		try{
			C0711_QuestTrackStatusNotifyMessage message = new C0711_QuestTrackStatusNotifyMessage();
			message.setQuestId(quest.getQuestId());
			message.setStatus((byte) QuestStatus.canSubmit.getType());
			GameContext.getMessageCenter().send("", role.getUserId(), message);
		}catch(Exception ex){
			logger.error(QuestHelper.class.getName() + ".pushQuestCanSubmitTipMessage error: ", ex);
		}
	}
	
	/**
	 * 任务失败进度（任务追踪更新）
	 * @param role
	 * @param quest
	 */
	public static void pushQuestFailureTipMessage(RoleInstance role,Quest quest){
		try{
			C0711_QuestTrackStatusNotifyMessage message = new C0711_QuestTrackStatusNotifyMessage();
			message.setQuestId(quest.getQuestId());
			message.setStatus((byte) QuestStatus.failure.getType());
			GameContext.getMessageCenter().send("", role.getUserId(), message);
		}catch(Exception ex){
			logger.error(QuestHelper.class.getName() + ".pushQuestFailureTipMessage error: ", ex);
		}
	}
	
	/**
	 * 发生任务进度提示（任务追踪更新）
	 * @param role
	 * @param quest
	 * @param currentNum
	 * @param term
	 */
	public static void pushQuestTipMessage(RoleInstance role, Quest quest, int currentNum, QuestTerm term, int termIndex){
		try{
			C0710_QuestTrackTermNotifyMessage message = new C0710_QuestTrackTermNotifyMessage();
			message.setQuestId(quest.getQuestId());
			message.setTermIndex((byte) termIndex);
			message.setCurrentNum((short) currentNum);
			GameContext.getMessageCenter().send("", role.getUserId(), message);
		}catch(Exception ex){
			logger.error(QuestHelper.class.getName() + ".pushQuestTipMessage error: ", ex);
		}
	}
	
	public static String getTimeLimitQuestTimeInfo(RoleInstance role,
			Quest quest,boolean showFailureAndColor) {
		if (quest.getTimeLimit() <= 0) {
			return "";
		}
		// 显示
		RoleQuestLogInfo logInfo = role.getQuestLogMap()
				.get(quest.getQuestId());
		if (null == logInfo) {
			return "";
		}
		int status = QuestHelper.getQuestStatus(role, quest, logInfo).getType();
		String failureColor = "\n[\\C]FFFF0000[C]" + GameContext.getI18n().getText(TextId.QUEST_TIME_FAILURE_TIPS);
		if(status == QuestStatus.failure.getType()){
			return showFailureAndColor ? failureColor :"";
		}
		long useTime = System.currentTimeMillis() - logInfo.getCreateTime().getTime();
		long reTime = quest.getTimeLimit() * 60 * 1000 - useTime;
		if(reTime < 0 &&  status == QuestStatus.notComplete.getType()){
			return showFailureAndColor ? failureColor :"";
		}
		int value = 0;
		String unit = GameContext.getI18n().getText(TextId.MINUTE);
		if (reTime >= ONE_MINUTE) {
			value = (int) Math.ceil(reTime / (float) 1000 / 60);
		} else {
			value = (int) Math.ceil(reTime / (float) 1000);
			unit = GameContext.getI18n().getText(TextId.SEC);
		}
		if(value <0){
			value = 0 ;
		}
		return "\n" + (showFailureAndColor ? "[\\C]FFedba61[C]" :"") + GameContext.getI18n().getText(TextId.QUEST_REMAIN_TIME) + ": " + value + unit;
	}
	/**
	 * 根据用户请求的参数构建任务信息
	 * @param role
	 * @param reqParam
	 * @return
	 */
	public static C0703_QuestBeforeOperateRespMessage questBeforeOperateRespMessageBuilder(RoleInstance role,String reqParam){
		C0703_QuestBeforeOperateRespMessage respMsg = new C0703_QuestBeforeOperateRespMessage();
		Map<String,String> paramMap = Util.urlParamParser(reqParam);
		int questId = Integer.parseInt(paramMap.get(QuestHelper.QuestId_Key).toString());
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		byte status = Byte.parseByte(paramMap.get(QuestHelper.QuestStatus_Key).toString());
		respMsg.setStatus(status);
        int where = QuestHelper.Where.Submit.getType() ;
        if(status == QuestStatus.canAccept.getType()){
            where = QuestHelper.Where.Accept.getType();
        }else if(status == QuestStatus.notComplete.getType()){
        	where = QuestHelper.Where.View.getType();
        }
        QuestInfoItem questInfoItem = QuestHelper.getQuestInfoItem(role, quest, where);
		if(where == QuestHelper.Where.View.getType() && quest.getTimeLimit()>0){
			questInfoItem.setDesc(questInfoItem.getDesc() + getTimeLimitQuestTimeInfo(role,quest,true));
		}
		respMsg.setQuestInfoItem(questInfoItem);
		return respMsg ;
	}
	
	/**
	 * 物品触发任务时,给用户下发消息构建
	 * @param role
	 * @param goods
	 * @return
	 */
	public static C0703_QuestBeforeOperateRespMessage questBeforeOperateRespMessageBuilder4GoodsTigger(
			RoleInstance role,
			Quest quest,
			GoodsTaskprops goods){
		C0703_QuestBeforeOperateRespMessage respMsg = new C0703_QuestBeforeOperateRespMessage();
		byte status = (byte)QuestStatus.canAccept.getType();
		respMsg.setStatus(status);
		//构建参数
		respMsg.setParam(QuestGoodsTriggerGoodsId_Key + "=" + goods.getId());
		respMsg.setQuestInfoItem(QuestHelper.getQuestInfoItem(role, quest,QuestHelper.Where.Accept.getType()));
		return respMsg ;
	}
	
	public enum Where {
		Accept(0),
		View(1),
		Submit(2),
		;
		
		private final int type;
		
		Where(int type){
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
	}
	
	/**
	 * 获取任务状态
	 * @param role
	 * @param quest
	 * @return
	 */
	public static QuestStatus getQuestStatus(RoleInstance role, Quest quest){
		if(null == role || null == quest){
			return QuestStatus.noneTask;
		}
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(quest.getQuestId());
		return QuestHelper.getQuestStatus(role, quest, logInfo);
	}
	
	/**
	 * 其实取当前阶段是否完成就好
	 * 因为判断当前阶段是否完成有消耗
	 * 为了性能才有这方法
	 * @param role
	 * @param quest
	 * @param logInfo
	 * @return
	 */
	public static QuestStatus getQuestStatus(RoleInstance role,Quest quest,RoleQuestLogInfo logInfo){
		if(null == logInfo){
			if(quest.canAccept(role)){
				return QuestStatus.canAccept;
			}
			return QuestStatus.noneTask;
		}
		if(logInfo.getStatus() == QuestStatus.failure.getType()){
			return QuestStatus.failure;
		}
        List<QuestPhase> phaseList = quest.getPhaseList() ;
        if(logInfo.getPhase() == phaseList.size()-1 &&
                phaseList.get(phaseList.size()-1).isPhaseComplete(role)){
            //在最后一阶段
            return QuestStatus.canSubmit;
        }
		return QuestStatus.get(logInfo.getStatus());
	}
	
	/**
	 * 判断任务的物品条件中是否包含某goodsId条件
	 * @param quest
	 * @param goodsId
	 * @return
	 */
	public static boolean hasGoodsEffect(Quest quest,int goodsId){
		if(!quest.hasGoodsEffect){
			return false ;
		}
		if(isLetterQuest(quest)){
			return true ;
		}
		QuestPhase phase = quest.getPhaseList().get(1);
		List<QuestTerm> termList = phase.termList();
		for (QuestTerm term : termList) {
			if(!term.getQuestTermType().isNeedGoods()){
				continue ;
			}
			if(!term.getParameter().equals(String.valueOf(goodsId))){
				continue ;
			}
			return true ;
		}
		return false ;
	}
	
	public static boolean isLetterQuest(Quest quest){
		if( 2 != quest.getPhaseList().size()){
			return false ;
		}
		QuestPhase firstPhase = quest.getPhaseList().get(0);
		if(!(firstPhase instanceof AcceptQuestPhase)){
			return false ;
		}
		AcceptQuestPhase aqp = (AcceptQuestPhase)firstPhase;
		return aqp.getLetterGoodsId() > 0 ;
	}
	
	
	public static boolean isLetterGoodsExists(RoleInstance role,Quest quest){
		if(isLetterQuest(quest) && 
				1 > role.getRoleBackpack().countByGoodsId(
						((AcceptQuestPhase)quest.phaseList.get(0)).getLetterGoodsId())
		){
			//信件被丢弃
			return false ;
		}
		return true ;
	}
	
	/**
	 * 判断任务的物品条件是否已经都完成
	 * @param role
	 * @param quest
	 * @return
	 */
	public static boolean isGoodsTemsComplete(RoleInstance role,Quest quest){
		if(!quest.hasGoodsEffect){
			return true ;
		}
		if(!isLetterGoodsExists(role,quest)){
			//信件被丢弃
			return false ;
		}
		QuestPhase phase = quest.getPhaseList().get(1);
		List<QuestTerm> termList = phase.termList();
		for (int index = 0; index < termList.size(); index++) {
			QuestTerm term = termList.get(index);
			if(!term.getQuestTermType().isNeedGoods()){
				continue ;
			}
			if(term.getCount() > phase.getCurrentNum(role, index)){
				return false ;
			}
		}
		return true ;
	}
	/**
	 * 判断任务条件是否都已经完成
	 * @param role
	 * @param quest
	 * @return
	 */
	public static boolean isAttributeComplete(RoleInstance role,Quest quest){
		QuestPhase phase = quest.getPhaseList().get(1);
		List<QuestTerm> termList = phase.termList();
		for (int index = 0; index < termList.size(); index++) {
			QuestTerm term = termList.get(index);
			if(term.getQuestTermType() != QuestTermType.Attribute){
				continue ;
			}
			if(term.getCount() > phase.getCurrentNum(role, index)){
				return false ;
			}
		}
		return true ;
	}
	
	/**
	 * 判断阶段条件是否完成
	 * 是否是物品条件并且都已经完成
	 * 属性阶段是否都已经完成
	 * 属性类型、组合任务、副本次数、活跃度数量、随机任务次数、好友数量
	 * @param role
	 * @param phase
	 * @return
	 */
	public static boolean isPhaseTermsComplete(RoleInstance role, QuestPhase phase){
		if(!phase.isHasGoodsEffect() && !phase.isSpecialCurrCount()){
			return false;
		}
		List<QuestTerm> termList = phase.termList();
		for (int index = 0; index < termList.size(); index++) {
			QuestTerm term = termList.get(index);
			if(!term.getQuestTermType().isSpecial()){
				return false;
			}
			if(term.getCount() > phase.getCurrentNum(role, index)){
				return false ;
			}
		}
		return true;
	}
	
	public static boolean isPhaseComplete(RoleInstance role,Quest quest,
			QuestPhase currentPhase, List<QuestTerm> termList) throws ServiceException {
		//GameContext context = GameContext.getGameContext();
		int questId = quest.getQuestId();
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
		if (logInfo.getPhase() > currentPhase.getPhase()) {
			return true;
		} else if (logInfo.getPhase() < currentPhase.getPhase()) {
			return false;
		}
		// 任务的当前阶段是自己,需要判断
		for (int index = 0; index < termList.size(); index++) {
			QuestTerm term = termList.get(index);
			/*if (term.getCount() > context.getUserQuestApp().getQuestLogValue(role, questId,
					index)) {
				return false;
			}*/
			//物品有可能会丢弃，所以不能调用上面接口
			if(term.getCount() > currentPhase.getCurrentNum(role, index)){
				return false ;
			}
		}
		return true;
	
	}
	
	/**
	 * 接任务参数分析器
	 * @param param
	 * @return
	 */
	public static QuestAcceptParam questAcceptParamParser(String param){
		QuestAcceptParam  ret = new QuestAcceptParam();
		if(Util.isEmpty(param)){
			ret.setAcceptModel(QuestAcceptParam.Model.Normal);
			return ret ;
		}
		Map<String,String> map = Util.urlParamParser(param);
		if(map.containsKey(QuestGoodsTriggerGoodsId_Key)){
			//任务触发
			ret.setAcceptModel(QuestAcceptParam.Model.Goods);
			ret.setTiggerGoodsId(Integer.parseInt(map.get(QuestGoodsTriggerGoodsId_Key)));
			return ret ;
		}
		if(map.containsKey(QuestSharedRoleId_Key)){
			//共享触发
			ret.setAcceptModel(QuestAcceptParam.Model.Share);
			ret.setShareRoleId(map.get(QuestSharedRoleId_Key));
			if(map.containsKey(QuestSharedCancel_Key)){
				String cancel = map.get(QuestSharedCancel_Key).toLowerCase().trim();
				if("1".equals(cancel) || "true".equals(cancel)){
					ret.setShareCancel(true);
				}
			}
			return ret ;
		}
		ret.setAcceptModel(QuestAcceptParam.Model.Normal);
		return ret ;
	}
	
	/**
	 * 构建任务信息
	 * @param role
	 * @param quest
	 * @param where
	 * @return
	 */
	public static QuestInfoItem getQuestInfoItem(RoleInstance role, Quest quest, int where) {
		QuestInfoItem questInfoItem = new QuestInfoItem();
		questInfoItem.setQuestId(quest.getQuestId());
		questInfoItem.setQuestName(quest.getQuestName());
		int minLevel = quest.getMinLevel();
		questInfoItem.setMinLevel((byte) minLevel);
		questInfoItem.setMaxLevel((byte) quest.getMaxLevel());
		//接任务类型
		questInfoItem.setAcceptType(quest.getQuestAcceptType().getType());
		List<QuestPhase> phaseList = quest.getPhaseList();
		int phaseSize = phaseList.size();
		AcceptQuestPhase acceptPhase = (AcceptQuestPhase) phaseList.get(0);
		SubmitQuestPhase submitPhase = (SubmitQuestPhase) phaseList.get(phaseSize-1);
		//接任务NPC
		String acceptNpcId = acceptPhase.getNpcId();
		questInfoItem.setAcceptNpcId(acceptNpcId);
		NpcTemplate acceptNpc = GameContext.getNpcApp().getNpcTemplate(acceptNpcId);
		if(null != acceptNpc){
			questInfoItem.setAcceptNpcName(acceptNpc.getNpcname());
		}
		//交任务NPC
		String submitNpcId = submitPhase.getNpcId();
		questInfoItem.setSubmitNpcId(submitNpcId);
		NpcTemplate submitNpc = GameContext.getNpcApp().getNpcTemplate(submitNpcId);
		if(null != submitNpc){
			questInfoItem.setSubmitNpcName(submitNpc.getNpcname());
		}
		//接任务场景名称
		sacred.alliance.magic.app.map.Map acceptMap = GameContext.getMapApp().getMap(acceptPhase.getMapId());
		if(null != acceptMap){
			questInfoItem.setMapName(acceptMap.getMapConfig().getMapdisplayname());
		}
		//任务描述
		if (where == QuestHelper.Where.Accept.getType()) {
			questInfoItem.setDesc(quest.getQuestDesc());
		} else if (where == QuestHelper.Where.Submit.getType()) {
			questInfoItem.setDesc(submitPhase.getDialogContent());
		} else if (where == QuestHelper.Where.View.getType()) {
			questInfoItem.setDesc(quest.getTargetDesc());
		}
		questInfoItem.setAcMapId(acceptPhase.getMapId());
		questInfoItem.setSmMapId(submitPhase.getMapId());
		//任务奖励
		QuestAward award = quest.getQuestAward(role);
		if (null != award) {
			questInfoItem.setAttrAwardList(award.getAttrAwardList());
			questInfoItem.setGoodsAwardList(award.getGoodsAwardList(role));
		}
		//任务条件
		questInfoItem.setTermItems(QuestHelper.getQuestTermItemList(role, quest));
		return questInfoItem;
	}
	
	public static QuestTrackItem getQuestTrackInfo(RoleInstance role, Quest quest){
		int questId = quest.getQuestId();
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(questId);
		QuestTrackItem trackItem = new QuestTrackItem();
		trackItem.setQuestId(questId);
		trackItem.setQuestType((byte) quest.getQuestType().getType());
		trackItem.setQuestName(quest.getQuestName());
		trackItem.setAcceptType(quest.getQuestAcceptType().getType());
		QuestStatus questStatus = QuestHelper.getQuestStatus(role, quest, logInfo);
		List<QuestPhase> phaseList = quest.getPhaseList();
		if(null == logInfo){
			//任务未接时，发接任务的NPC名字
			NpcTemplate acceptNpc = GameContext.getNpcApp().getNpcTemplate(quest.getAcceptNpcId());
			if(null != acceptNpc){
				trackItem.setSubmitNpcName(acceptNpc.getNpcname());
			}
			AcceptQuestPhase acceptPhase = (AcceptQuestPhase) phaseList.get(0);
			trackItem.setSmMapId(acceptPhase.getMapId());
		}else{
			NpcTemplate submitNpc = GameContext.getNpcApp().getNpcTemplate(quest.getSubmitNpcId());
			if(null != submitNpc){
				trackItem.setSubmitNpcName(submitNpc.getNpcname());
			}
			int phaseSize = phaseList.size();
			SubmitQuestPhase submitPhase = (SubmitQuestPhase) phaseList.get(phaseSize-1);
			trackItem.setSmMapId(submitPhase.getMapId());
		}
		trackItem.setStatus((byte) questStatus.getType());
		//任务条件
		trackItem.setTermItems(QuestHelper.getQuestTermItemList(role, quest));
		return trackItem;
	}
	
	/**
	 * 获取任务条件列表
	 * @param quest
	 * @return
	 */
	public static List<QuestTerm> getQuestTermList(Quest quest){
		if(null == quest){
			return new ArrayList<QuestTerm>();
		}
		QuestPhase phase = quest.getPhaseList().get(0);
		if (phase instanceof AcceptQuestPhase) {
			phase = quest.getPhaseList().get(1);
		}
		return phase.termList();
	}
	
	/**
	 * 获取任务条件的Message
	 * @param role
	 * @param quest
	 * @return
	 */
	public static List<QuestTermItem> getQuestTermItemList(RoleInstance role, Quest quest){
		List<QuestTermItem> list = new ArrayList<QuestTermItem>();
		if(null == role || null == quest){
			return list;
		}
		List<QuestTerm> termList = QuestHelper.getQuestTermList(quest);
		if(Util.isEmpty(termList)){
			return list;
		}
		boolean canSubmit = quest.canSubmit(role);
		for(int i=0; i<termList.size(); i++){
			QuestTerm term = termList.get(i);
			if(null == term){
				continue;
			}
			list.add(term.getQuestTermItem(role, quest, canSubmit, i));
		}
		return list;
	}
	
}
