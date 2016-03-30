package com.game.draco.app.quest.poker;

import java.util.Collection;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.poker.config.QuestPokerAwardRatioConfig;
import com.game.draco.app.quest.poker.config.RmQuestAwardConfig;
import com.game.draco.app.quest.poker.domian.RoleQuestPoker;
import com.game.draco.app.quest.poker.vo.QuestPokerRoleData;
import com.game.draco.message.request.C0712_QuestPokerPanelReqMessage;

public interface QuestPokerApp extends Service, AppSupport {
	
	public static final short RmQuestPanelReqCmdId = new C0712_QuestPokerPanelReqMessage().getCommandId();
	
	public Message getQuestPokerPanelMessage(RoleInstance role,boolean isManual);
	
	public Result openPoker(RoleInstance role, byte index);
	
	public Result accept(RoleInstance role);
	
	public Result submit(RoleInstance role);
	
	public Result refresh(RoleInstance role);

	public Result refreshQuest(RoleInstance role);

	public int getMaxConfigCount();
	
	public int getCurrCount(RoleInstance role);
	
	public String getDescribe();
	
	public QuestPokerRoleData getQuestPokerRoleData(String roleId);
	
	public RmQuestAwardConfig getRmQuestAwardConfig(int awardId);
	
	public RoleQuestLogInfo getCurrPokerQuestLog(RoleInstance role);
	
	public Map<Integer,Integer> getPokerSecondWeightMap();
	
	public Map<Integer,Integer> getPokerThirdWeightMap(PokerTwoType twoType);
	
	public void sendThreePokerReward(RoleInstance role, int pokerVal1, int pokerVal2, int pokerVal3);
	
	public Collection<QuestPokerAwardRatioConfig> getPokerAwardRatioList();
	
	public void giveUpPokerQuest(RoleInstance role);
	
	public void onQuestPokerDataReset(RoleInstance role,RoleQuestPoker poker) ;

	/**
	 * 购买每日任务（翻牌）的轮数（每轮三张牌）
	 * @param role
	 * @param confirm
	 * @return
	 * @date 2014-10-30 上午10:47:43
	 */
	public Result buyCountTime(RoleInstance role, byte confirm);
	/**
	 * 得到玩家在购买轮数最后的今天最大轮数限制
	 * @param role
	 * @return
	 * @date 2014-10-30 上午11:13:29
	 */
	public int getTotalTime(RoleInstance role);
	
}
