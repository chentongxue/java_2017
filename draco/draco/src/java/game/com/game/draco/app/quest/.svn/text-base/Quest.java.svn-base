package com.game.draco.app.quest;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.base.QuestAcceptType;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.poker.config.RmQuestAwardConfig;
import com.game.draco.base.CampType;

public abstract class Quest {
	
	protected final static Logger logger = LoggerFactory.getLogger(Quest.class);
	
	protected int questId;// 任务ID
	protected String questName;//任务名
	protected String questDesc ; //任务描述
	protected String targetDesc ; //任务目标描述
	protected boolean canRepeat;// 是否可重复
	protected boolean canGiveUp;//任务是否可以放弃
	protected int questLevel;//任务级别，方便搜索
	
	protected QuestType questType = QuestType.MainLine;//任务类型,默认主线任务
	protected QuestAcceptType questAcceptType = QuestAcceptType.Npc;//接任务类型[默认在NPC处接]
	
	protected boolean beClosed;//当前此任务关闭默认为false
	protected int minLevel;//玩家等级下限
	protected int maxLevel;//玩家不能超过的最大等级
	
	protected int premiseQuestId; //前提任务Id
	protected int nextQuestId;//后续任务ID
	protected int chainIndex;//任务链索引
	
	protected int premiseGoodsId; //前提物品ID
	protected int timeLimit ; //限时(分钟)
	protected CampType campType = null;//阵营限制
	protected int titleId = 0;//所需称号Id
	protected boolean isPrintLog = false;//是否打印日志
	
	protected String acceptNpcId;//接任务npcId
	protected String submitNpcId;//交任务npcId
	protected String acceptMapId;//接任务的地图ID
	protected String submitMapId;//交任务的地图ID
	
	/**角色死亡是否失败*/
	protected boolean deathWillFailure = false ;

	/**任务奖励*/
	protected QuestAward award ;
	
	protected List<QuestPhase> phaseList ;
	
	protected short activeId = 0;//活动ID
	
	protected int relyGoodsId = 0;//触发任务的道具ID
	
	/** 互斥任务id，多个之间用逗号分隔 */
	protected String huchiId  = "";
	
	/** 完成任务时给角色增加的buff，0是没有 */
	protected short completeBuff = 0;
	
	/** VIP等级限制 */
	protected Set<Integer> vipLevelSet;
	
	/** 注册渠道限制 */
	protected Set<Integer> regChannelSet;
	
	/** 登录渠道限制 */
	protected Set<Integer> loginChannelSet;
	
	protected static GameContext context = GameContext.getGameContext();
	
	public Quest(int questId){
		this.questId = questId;
	}
	
	/** 是否是主线任务 **/
	public boolean isMainLine(){
		return QuestType.MainLine == this.questType;
	}
	
	/** 任务是否是日常任务 **/
	public boolean isDailyQuest() {
		return QuestType.Daily == this.questType;
	}
	
	/** 是否在活动中的任务 **/
	public boolean isInActive(){
		return this.activeId > 0;
	}
	
	public abstract boolean isLastPhase(int phase);
	
	public abstract void addPhase(QuestPhase phase);
	
	public abstract void enterMap(RoleInstance role);
	
	public abstract void killMonster(RoleInstance role, String npcId);
	
	public abstract List<GoodsOperateBean> getQuestFall(RoleInstance role, String npcId);
	
	public abstract void triggerEvent(RoleInstance role,String eventId);
	
	public abstract void death(RoleInstance role);
	
	public abstract void update(RoleInstance role);
	
	public abstract void useGoods(RoleInstance role, int goodsId);
	
	public abstract void getGoods(RoleInstance role, int goodsId,int goodsNum);
	
	public abstract void getAttribute(RoleInstance role, int type);
	
	/** 是否可被看到 */
	public abstract boolean canView(RoleInstance role);
	
	public abstract boolean canAccept(RoleInstance role);
	
	public abstract boolean canSubmit(RoleInstance role);
	
	/** 丢弃物品通知任务变化 */
	public abstract int discardGoodsNotify(RoleInstance role, int goodsTemplateId) ;
	
	public abstract int discardAttributeNotify(RoleInstance role);
	
	public abstract void killRole(RoleInstance role);
	
	/** 选择菜单 */
	public abstract void choseMenu(RoleInstance role, int menuId);
	
	/**获得任务当前阶段条件*/
	public abstract List<QuestTerm> getTermList(RoleInstance role);
	
	/** 副本通关 */
	public abstract void copyPass(RoleInstance role, short copyId);
	
	/** 副本某地图通关 */
	public abstract void copyMapPass(RoleInstance role, String mapId);
	
	/** 地图刷怪波次 */
	public abstract void mapRefreshNpc(RoleInstance role, int refreshIndex);
	
	/**
	 * 获得用户当前完成情况
	 * @param role
	 * @return
	 */
	public abstract int[] getCurrentComplete(RoleInstance role);
	
	/**
	 * 检测相关参数(包括逻辑相关)
	 * @return
	 */
	public abstract void verify() ;
	
	public abstract void init() ;
	
	public abstract QuestPhase getCurrentPhase(RoleInstance role);
	
	public abstract void giveUp(RoleInstance role);
	
	protected boolean hasGoodsEffect = false ;

	public boolean isHasGoodsEffect() {
		return hasGoodsEffect;
	}
	
	public String getQuestDesc() {
		return questDesc;
	}

	public void setQuestDesc(String questDesc) {
		this.questDesc = questDesc;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	public String getQuestName() {
		return questName;
	}

	public void setQuestName(String questName) {
		this.questName = questName;
	}

	public boolean isCanRepeat() {
		return canRepeat;
	}

	public void setCanRepeat(boolean canRepeat) {
		this.canRepeat = canRepeat;
	}

	public boolean isCanGiveUp() {
		return canGiveUp;
	}

	public void setCanGiveUp(boolean canGiveUp) {
		this.canGiveUp = canGiveUp;
	}

	public int getQuestLevel() {
		return questLevel;
	}
	
	public void setQuestLevel(int questLevel) {
		this.questLevel = questLevel;
	}
	
	public boolean isBeClosed() {
		return beClosed;
	}

	public void setBeClosed(boolean beClosed) {
		this.beClosed = beClosed;
	}
	public List<QuestPhase> getPhaseList() {
		return phaseList;
	}

	public void setPhaseList(List<QuestPhase> phaseList) {
		if(null == phaseList){
			this.phaseList = null ;
			return ;
		}
		for(QuestPhase phase : phaseList){
			this.addPhase(phase);
		}
	}

	public String getTargetDesc() {
		return targetDesc;
	}

	public void setTargetDesc(String targetDesc) {
		this.targetDesc = targetDesc;
	}

	public int getPremiseQuestId() {
		return premiseQuestId;
	}

	public void setPremiseQuestId(int premiseQuestId) {
		this.premiseQuestId = premiseQuestId;
	}

	public boolean isDeathWillFailure() {
		return deathWillFailure;
	}

	public void setDeathWillFailure(boolean deathWillFailure) {
		this.deathWillFailure = deathWillFailure;
	}

	/**
	 * 获取任务奖励
	 * 随机任务则取角色任务日志上的奖励
	 * @param role
	 * @return
	 */
	public QuestAward getQuestAward(RoleInstance role){
		//从NPC处接取的任务，取普通的任务奖励
		if(QuestAcceptType.Npc == this.questAcceptType){
			return this.award;
		}
		//非随机任务类型，取普通的任务奖励
		if(QuestAcceptType.Poker != this.questAcceptType){
			return this.award;
		}
		RoleQuestLogInfo logInfo = GameContext.getQuestPokerApp().getCurrPokerQuestLog(role);
		if(null == logInfo){
			return this.award;
		}
		RmQuestAwardConfig rmAward = GameContext.getQuestPokerApp().getRmQuestAwardConfig(logInfo.getAwardId());
		if(null == rmAward){
			return this.award;
		}
		return rmAward.getQuestAward();
	}
	
	public QuestAward getAward() {
		return award;
	}

	public void setAward(QuestAward award) {
		this.award = award;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public QuestType getQuestType() {
		return questType;
	}

	public void setQuestType(QuestType questType) {
		this.questType = questType;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getPremiseGoodsId() {
		return premiseGoodsId;
	}

	public void setPremiseGoodsId(int premiseGoodsId) {
		this.premiseGoodsId = premiseGoodsId;
	}

	public GameContext getContext() {
		return context;
	}
	
	public boolean isPrintLog() {
		return isPrintLog;
	}

	public void setPrintLog(boolean isPrintLog) {
		this.isPrintLog = isPrintLog;
	}

	public String getAcceptNpcId() {
		return acceptNpcId;
	}

	public void setAcceptNpcId(String acceptNpcId) {
		this.acceptNpcId = acceptNpcId;
	}

	public String getSubmitNpcId() {
		return submitNpcId;
	}

	public void setSubmitNpcId(String submitNpcId) {
		this.submitNpcId = submitNpcId;
	}

	public String getHuchiId() {
		return huchiId;
	}

	public void setHuchiId(String huchiId) {
		this.huchiId = huchiId;
	}

	public short getCompleteBuff() {
		return completeBuff;
	}

	public void setCompleteBuff(short completeBuff) {
		this.completeBuff = completeBuff;
	}
	
	public short getActiveId() {
		return activeId;
	}

	public void setActiveId(short activeId) {
		this.activeId = activeId;
	}

	public int getRelyGoodsId() {
		return relyGoodsId;
	}

	public void setRelyGoodsId(int relyGoodsId) {
		this.relyGoodsId = relyGoodsId;
	}

	public QuestAcceptType getQuestAcceptType() {
		return questAcceptType;
	}

	public void setQuestAcceptType(QuestAcceptType questAcceptType) {
		this.questAcceptType = questAcceptType;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public Set<Integer> getVipLevelSet() {
		return vipLevelSet;
	}

	public void setVipLevelSet(Set<Integer> vipLevelSet) {
		this.vipLevelSet = vipLevelSet;
	}

	public Set<Integer> getRegChannelSet() {
		return regChannelSet;
	}

	public void setRegChannelSet(Set<Integer> regChannelSet) {
		this.regChannelSet = regChannelSet;
	}

	public Set<Integer> getLoginChannelSet() {
		return loginChannelSet;
	}

	public void setLoginChannelSet(Set<Integer> loginChannelSet) {
		this.loginChannelSet = loginChannelSet;
	}

	public CampType getCampType() {
		return campType;
	}

	public void setCampType(CampType campType) {
		this.campType = campType;
	}

	public String getAcceptMapId() {
		return acceptMapId;
	}

	public void setAcceptMapId(String acceptMapId) {
		this.acceptMapId = acceptMapId;
	}

	public String getSubmitMapId() {
		return submitMapId;
	}

	public void setSubmitMapId(String submitMapId) {
		this.submitMapId = submitMapId;
	}

	public int getNextQuestId() {
		return nextQuestId;
	}

	public void setNextQuestId(int nextQuestId) {
		this.nextQuestId = nextQuestId;
	}

	public int getChainIndex() {
		return chainIndex;
	}

	public void setChainIndex(int chainIndex) {
		this.chainIndex = chainIndex;
	}

}
