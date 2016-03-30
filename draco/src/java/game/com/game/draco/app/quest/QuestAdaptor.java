package com.game.draco.app.quest;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.CollectPointNotifyType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.phase.AcceptQuestPhase;
import com.game.draco.app.quest.phase.SubmitQuestPhase;
import com.game.draco.message.push.C0709_QuestTrackUpdateNotifyMessage;

public class QuestAdaptor extends Quest {

	public QuestAdaptor(int questId) {
		super(questId);
	}
	
	public void update(RoleInstance role) {
		QuestPhase phase = getCurrentPhase(role);
		phase.update(role);
	}
	
	@Override
	public void enterMap(RoleInstance role) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.enterMap(role);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}

	@Override
	public void useGoods(RoleInstance role, int goodsId) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.useGoods(role, goodsId);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}

	@Override
	public void getGoods(RoleInstance role, int goodsId, int goodsNum) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.getGoods(role, goodsId, goodsNum);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}
	
	@Override
	public void getAttribute(RoleInstance role, int type) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.getAttribute(role, type);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}
	
	@Override
	public void killMonster(RoleInstance role, String npcId) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.killMonster(role, npcId);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
		//杀怪限制阶段
		ret = phase.killMonsterLimit(role, npcId);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
		//杀怪收集物品（不掉落只按几率计数）
		ret = phase.killNpcFallCount(role, npcId);
		if(ret > 0){
			this.nextPhaseCursor(role, phase);
		}
	}
	
	@Override
	public void killRole(RoleInstance role) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.killRole(role);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}
	
	@Override
	public void triggerEvent(RoleInstance role, String eventId) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.triggerEvent(role, eventId);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}

	public void death(RoleInstance role) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		if (phase.master.isDeathWillFailure()) {
			// 任务失败
			try {
				context.getUserQuestApp().updateQuestStatus(role,
						questId, QuestStatus.failure);
			} catch (ServiceException e) {
			}
			return;
		}
		int ret = phase.death(role);
		if (ret > 0) {
			this.nextPhaseCursor(role, phase);
		}
	}

	/**
	 * 将任务的游标后移(如果当前是最后一阶段则不处理)
	 * 
	 * @param role
	 * @param currentPhase
	 */
	private void nextPhaseCursor(RoleInstance role, QuestPhase currentPhase) {
		try {
			if (null == currentPhase) {
				return;
			}
			if (!currentPhase.isPhaseComplete(role)) {
				return;
			}
			if (this.isLastPhase(currentPhase.getPhase())) {
				// 最后一阶段,任务已经完成
				context.getUserQuestApp().updateQuestStatus(role,
						questId, QuestStatus.canSubmit);
				return;
			}

			QuestStatus status = QuestStatus.notComplete;
			if (currentPhase.getPhase() == phaseList.size() - 2
					&& phaseList.get(phaseList.size() - 1)
							.isPhaseComplete(role)) {
				// 当前已经处于倒数第二阶段,而且最后一阶段已经完成
				status = QuestStatus.canSubmit;
			}
			// 移动到后一阶段
			context.getUserQuestApp().insertOrUpdateQuestLog(role, questId,
					currentPhase.getPhase() + 1, 0, 0, 0, status);

			if (status != QuestStatus.canSubmit) {
				return;
			}
			// 提示任务完成已经可提交
			QuestHelper.pushQuestCanSubmitTipMessage(role, this);
			// 发送Npc头顶标识
			// 只有最后阶段是提交任务阶段并且提交任务Npc有可能在用户当前地图出生才push
			this.refreshNpcHead(role);
			context.getUserQuestApp().refreshPoint(role, CollectPointNotifyType.CollectUnable, this);
		} catch (ServiceException e) {
			logger.error(this.getClass().getName() + ".nextPhaseCursor error: ", e);
		}
	}
	
	/**
	 * 刷新NPC头顶标识
	 * @param role
	 */
	private void refreshNpcHead(RoleInstance role){
		// 发送Npc头顶标识
		// 只有最后阶段是提交任务阶段并且提交任务Npc有可能在用户当前地图出生才push
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return ;
		}
		QuestPhase lastPhase = phaseList.get(phaseList.size() - 1);
		if (lastPhase instanceof SubmitQuestPhase
				&& mapInstance.getMap().haveBornNpc(((SubmitQuestPhase) lastPhase).getNpcId())) {
			context.getUserQuestApp().notifyQuestNpcHeadSign(role);
		}
	}

	@Override
	public void giveUp(RoleInstance role) {
		QuestPhase currentPhase = getCurrentPhase(role);
		for (QuestPhase phase : phaseList) {
			if (phase.getPhase() > currentPhase.getPhase()) {
				continue;
			}
			phase.giveUp(role);
		}
	}

	public QuestPhase getCurrentPhase(RoleInstance role) {
		RoleQuestLogInfo pojo;
		pojo = role.getQuestLogInfo(this.questId);
		if (pojo == null) {
			return phaseList.get(0);
		}
		if (pojo.getStatus() == 1 && this.isCanRepeat()) {
			return phaseList.get(0);
		}
		int phase = pojo.getPhase();
		return phaseList.size() > phase ? phaseList.get(phase) : null;
	}

	public final void addPhase(QuestPhase phase) {
		if (null == phase) {
			return;
		}
		if (phaseList == null) {
			phaseList = new ArrayList<QuestPhase>();
		}
		phaseList.add(phase);
		phase.setPhase(phaseList.size() - 1);
		phase.setMaster(this);
	}

	private int getPhaseCount() {
		if (phaseList == null) {
			return 0;
		}
		return phaseList.size();
	}

	public boolean isLastPhase(int phase) {
		return (phase + 1 >= this.getPhaseCount());
	}

	protected QuestPhase getPhase(int phase) {
		if (phaseList == null || phaseList.size() <= phase) {
			return null;
		}
		return phaseList.get(phase);
	}

	/**
	 * 判断是否能接任务
	 */
	@Override
	public boolean canAccept(RoleInstance role) {
		if (null == role) {
			return false;
		}
		if (this.isBeClosed()) {
			return false;
		}
		//判断是否可显示
		if (!this.canView(role)) {
			return false;
		}
		//判断玩家等级
		int roleLevel = role.getLevel();
		if (roleLevel < this.minLevel) {
			return false;
		}
		if (this.maxLevel != 0 && roleLevel > this.maxLevel) {
			return false;
		}
		if (role.hasReceiveQuestNow(this.questId)) {
			return false;
		}
		//判断是否完成过任务
		if(role.hasFinishQuest(this.questId)){
			return false;
		}
		//判断角色已经接受和完成的任务中是否与该任务互斥
		if(!Util.isEmpty(this.huchiId)){
			for(String id : this.huchiId.split(Cat.comma)){
				if(Util.isEmpty(id)){
					continue;
				}
				id = id.trim();
				if(role.getQuestLogMap().containsKey(Integer.valueOf(id))){
					return false;
				}
				if(role.hasFinishQuest(Integer.valueOf(id))){
					return false;
				}
			}
		}
		//判断前置任务条件
		if(this.premiseQuestId > 0) {
			if(!role.hasFinishQuest(this.premiseQuestId)){
				return false;
			}
		}
		// 判断前提物品条件
		if (this.premiseGoodsId > 0) {
			if (role.getRoleBackpack().countByGoodsId(this.premiseGoodsId) >= 1) {
				return false;
			}
		}
		return true;
	}

	protected RoleQuestLogInfo getThisLog(RoleInstance role) {
		return role.getQuestLogInfo(this.questId);
	}

	@Override
	public boolean canSubmit(RoleInstance role) {
		//任务已关闭
		if (this.isBeClosed()) {
			return false;
		}
		if (GameContext.getQuestApp().beClosed(this.questId)) {
			return false;
		}
		RoleQuestLogInfo log = this.getThisLog(role);
		if (null == log) {
			return false;
		}
		if (!this.isLastPhase(log.getPhase())) {
			return false;
		}
		// 判断早已失败情况
		if (QuestStatus.failure.getType() == log.getStatus()) {
			return false;
		}
		// 判断是否符合当前阶段的条件
		QuestPhase currentPhase = this.getCurrentPhase(role);
		if (null == currentPhase) {
			return false;
		}
		if (!currentPhase.isPhaseComplete(role)) {
			return false;
		}
		QuestPhase firstPhase = this.phaseList.get(0);
		if (2 == this.phaseList.size()
				&& firstPhase instanceof AcceptQuestPhase
				&& ((AcceptQuestPhase) firstPhase).getLetterGoodsId() > 0) {
			// 送信类任务只有2阶段
			// 送信类任务需要判断目前角色背包是否有信件
			return role.getRoleBackpack().countByGoodsId(((AcceptQuestPhase) firstPhase).getLetterGoodsId()) >= 1;
		}
		return true;
	}

	public void verify() {
		if (this.questId <= 0) {
			this.checkFail("must set the questId");
		}
		if (null == questType) {
			this.checkFail("must set the questType");
		}
		if (Util.isEmpty(phaseList)) {
			this.checkFail("must set the phaseList");
		}
		if (phaseList.size() > 3) {
			this.checkFail("the phaseList max size is 3,and current size is "
					+ phaseList.size());
		}
		for(QuestPhase phase : this.phaseList){
			if(null == phase){
				continue;
			}
			if(phase.isCheckFail()){
				this.checkFail("quest script phase error: questId=" + this.questId);
			}
		}
		// 保证不多余一个接任务阶段,一个交任务阶段
		int accept = 0;
		int submit = 0;
		int currentIndex = -1;
		for (QuestPhase phase : this.phaseList) {
			currentIndex++;
			if (phase instanceof AcceptQuestPhase) {
				if (0 != currentIndex) {
					this.checkFail("AcceptQuestPhase must at the first phase for quest: "
									+ this.questId);
				}
				accept++;
			} else if (phase instanceof SubmitQuestPhase) {
				if (this.phaseList.size() - 1 != currentIndex) {
					this.checkFail("SubmitQuestPhase must at the last phase for quest: "
									+ this.questId);
				}
				submit++;
			}
		}
		if (accept > 1 || submit > 1) {
			this.checkFail("too many AcceptQuestPhase or SubmitQuestPhase for quest: "
							+ this.questId);
		}
	}
	
	protected void checkFail(String err) {
		Log4jManager.CHECK.error(err);
		Log4jManager.checkFail();
	}

	public final void init() {
		for (QuestPhase phase : phaseList) {
			this.hasGoodsEffect = hasGoodsEffect || phase.isHasGoodsEffect();
			try {
				//将接任务的地图放到任务上
				if(phase instanceof AcceptQuestPhase){
					this.acceptMapId = phase.getMapId();
					continue;
				}
				//将交任务的地图放到任务上
				if(phase instanceof SubmitQuestPhase){
					this.submitMapId = phase.getMapId();
					continue;
				}
			} catch (RuntimeException e) {
				logger.error("", e);
			}
		}
	}

	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role, String npcId){
		// 获得当前阶段
		QuestPhase currentPhase = this.getCurrentPhase(role);
		if (null == currentPhase) {
			return new ArrayList<GoodsOperateBean>();
		}
		return currentPhase.getQuestFall(role, npcId);
	}

	@Override
	public int[] getCurrentComplete(RoleInstance role) {
		int[] value = new int[] { 0, 0, 0 };
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return value;
		}
		List<QuestTerm> termList = phase.termList();
		if (null == termList) {
			return value;
		}
		// 获得当前阶段的完成情况
		RoleQuestLogInfo log = this.getThisLog(role);
		if (null == log) {
			return value;
		}
		return log.getDataValues();
	}

	@Override
	public List<QuestTerm> getTermList(RoleInstance role) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return null;
		}
		return phase.termList();
	}

	@Override
	public int discardGoodsNotify(RoleInstance role, int goodsTemplateId) {
		// 送信类任务放弃信件不再将任务设置为失败,只是在提交任务的阶段需要判断角色背包是否有此物品
		// 首先判断是否接任务时系统给的信件
		/*
		 * QuestPhase firstPhase = this.phaseList.get(0); if(firstPhase
		 * instanceof AcceptQuestPhase &&
		 * ((AcceptQuestPhase)firstPhase).getLetterGoodsId() ==
		 * goodsTemplateId){ //如果是因为交任务而删除不应该调用此接口 Object local =
		 * ThreadLocalWrapper.get(); if( null == local ||
		 * !String.valueOf(this.questId).equals(local.toString())){ //任务失败 try {
		 * context.getUserQuestApp().insertOrUpdateUserQuestLog(role,
		 * this.getQuestId(), QuestStatus.failure); //发送失败提示信息
		 * QuestHelper.pushQuestFailureTipMessage(role, this); } catch
		 * (ServiceException e) { e.printStackTrace(); } return 1 ; } }
		 */
		if (!QuestHelper.hasGoodsEffect(this, goodsTemplateId)) {
			return 0;
		}
		// 判断用户是否当前阶段
		QuestPhase currentPhase = this.getCurrentPhase(role);
		int result = this.prePhaseCursor4Goods(role, currentPhase);
		//通知任务追踪变化
		if(1 == result){
			this.notifyQuestTrackInfo(role);
		}
		return result;
	}

	@Override
	public int discardAttributeNotify(RoleInstance role) {
		try {
			// 判断用户是否当前阶段
			QuestPhase currentPhase = this.getCurrentPhase(role);
			if (null == currentPhase) {
				return 0;
			}
			if (!this.isLastPhase(currentPhase.phase)) {
				return 0;
			}
			// 判断是否符合条件,如果不符合将当前指针前移
			boolean complete = QuestHelper.isAttributeComplete(role, this);
			if (complete) {
				return 0;
			}
			
			QuestPhase prePhase = this.getPhase(currentPhase.phase - 1);
			int[] status = new int[] { 0, 0, 0 };
			int index = -1;
			for (QuestTerm term : prePhase.termList()) {
				index++;
				status[index] = term.getCount();
			}
			context.getUserQuestApp().insertOrUpdateQuestLog(role, questId,
					currentPhase.phase - 1, status[0], status[1], status[2], QuestStatus.notComplete);
			//通知任务追踪变化
			this.notifyQuestTrackInfo(role);
			return 1;
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return 0;
	}
	
	/** 通知任务追踪变化 */
	private void notifyQuestTrackInfo(RoleInstance role){
		C0709_QuestTrackUpdateNotifyMessage trackNotifyMsg = new C0709_QuestTrackUpdateNotifyMessage();
		trackNotifyMsg.setQuestTrackItem(QuestHelper.getQuestTrackInfo(role, this));
		role.getBehavior().sendMessage(trackNotifyMsg);
	}
	
	private int prePhaseCursor4Goods(RoleInstance role, QuestPhase currentPhase) {
		try {
			if (null == currentPhase) {
				return 0;
			}
			if (!this.isLastPhase(currentPhase.phase)) {
				return 0;
			}
			// 判断是否符合物品条件,如果不符合将当前指针前移
			boolean complete = QuestHelper.isGoodsTemsComplete(role, this);
			if (complete) {
				// 即使掉落了其中一些物品,但依然符合交任务条件
				return 0;
			}
			// 丢弃物品使交任务条件不再满足
			// 将指针前移
			if (!QuestHelper.isLetterGoodsExists(role, currentPhase.master)) {
				// 信件不存在,将任务标识为不可提交
				context.getUserQuestApp().insertOrUpdateQuestLog(role,
						questId, 0, 0, 0, 0, QuestStatus.notComplete);
				return 1;
			}
			QuestPhase prePhase = this.getPhase(currentPhase.phase - 1);
			int[] status = new int[] { 0, 0, 0 };
			int index = -1;
			for (QuestTerm term : prePhase.termList()) {
				index++;
				status[index] = term.getCount();
			}
			context.getUserQuestApp().insertOrUpdateQuestLog(role, questId,
					currentPhase.phase - 1, status[0], status[1], status[2], QuestStatus.notComplete);
			return 1;
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return 0;
	}
	
	@Override
	public void choseMenu(RoleInstance role, int menuId) {
		QuestPhase phase = getCurrentPhase(role);
		if (null == phase) {
			return;
		}
		int ret = phase.chooseMenu(role, menuId);
		if(ret > 0){
			this.nextPhaseCursor(role, phase);
		}
	}

	@Override
	public boolean canView(RoleInstance role) {
		//任务被关闭
		if(GameContext.getQuestApp().beClosed(this.questId)){
			return false;
		}
		//主线任务
		if(QuestType.MainLine == this.questType){
			//不在任务链中
			if(this.chainIndex <= 0){
				return false;
			}
			//如果没有完成过，只能看到第一个主线任务。
			int lastQuestId = role.getLastFinishQuestId();
			if(lastQuestId <= 0 && this.questId != GameContext.getQuestServiceApp().getFirstMainQuestId()){
				return false;
			}
		}
		//判断阵营
		if(null != this.campType && this.campType.getType() != role.getCampId()){
			return false;
		}
		//是否拥有称号
		if(this.titleId > 0 && !GameContext.getTitleApp().isExistTitle(role, this.titleId)){
			return false;
		}
		//判断注册渠道限制
		if(!Util.isEmpty(this.regChannelSet)){
			if(!this.regChannelSet.contains(role.getRegChannelId())){
				return false;
			}
		}
		//判断登录渠道限制
		if(!Util.isEmpty(this.loginChannelSet)){
			if(!this.loginChannelSet.contains(role.getChannelId())){
				return false;
			}
		}
		//判断VIP等级限制
		if(!Util.isEmpty(this.vipLevelSet)){
			byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			if(!this.vipLevelSet.contains(vipLevel)){
				return false;
			}
		}
		return true;
	}

	@Override
	public void copyMapPass(RoleInstance role, String mapId) {
		QuestPhase phase = getCurrentPhase(role);
		if(null == phase){
			return;
		}
		int ret = phase.copyMapPass(role, mapId);
		if(ret > 0){
			this.nextPhaseCursor(role, phase);
		}
	}

	@Override
	public void mapRefreshNpc(RoleInstance role, int refreshIndex) {
		QuestPhase phase = getCurrentPhase(role);
		if(null == phase){
			return;
		}
		int ret = phase.mapRefreshNpc(role, refreshIndex);
		if(ret > 0){
			this.nextPhaseCursor(role, phase);
		}
	}

	@Override
	public void copyPass(RoleInstance role, short copyId) {
		QuestPhase phase = getCurrentPhase(role);
		if(null == phase){
			return;
		}
		int ret = phase.copyPass(role, copyId);
		if(ret > 0){
			this.nextPhaseCursor(role, phase);
		}
	}
	
}
