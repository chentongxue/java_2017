package com.game.draco.app.quest.poker;

import java.util.Collection;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.poker.config.QuestPokerAwardRatioConfig;
import com.game.draco.app.quest.poker.config.RmQuestAwardConfig;
import com.game.draco.app.quest.poker.vo.QuestPokerRoleData;
import com.game.draco.message.request.C0712_QuestPokerPanelReqMessage;

public interface QuestPokerApp extends Service {
	
	public static final short RmQuestPanelReqCmdId = new C0712_QuestPokerPanelReqMessage().getCommandId();
	
	public void login(RoleInstance role);
	
	public void logout(RoleInstance role);
	
	public Message getQuestPokerPanelMessage(RoleInstance role);
	
	public Result openPoker(RoleInstance role, byte index);
	
	public Result accept(RoleInstance role);
	
	public Result submit(RoleInstance role);
	
	public Result refresh(RoleInstance role);
	
	public int getMaxCount();
	
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
	
}
