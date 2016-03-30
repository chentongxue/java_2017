package com.game.draco.app.quest.poker.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.quest.poker.PokerHelper;
import com.game.draco.app.quest.poker.domian.RoleQuestPoker;
import com.game.draco.message.item.QuestPokerInfoItem;

public @Data class QuestPokerRoleData {
	
	private String roleId;
	private RoleQuestLogInfo questLogInfo;
	private RoleQuestPoker roleQuestPoker;
	
	public List<QuestPokerInfoItem> getFinishedPokerInfoList(){
		List<QuestPokerInfoItem> list = new ArrayList<QuestPokerInfoItem>();
		if(null == this.roleQuestPoker){
			return list;
		}
		for(int poker : this.roleQuestPoker.getFinishedPokerList()){
			list.add(this.buildQuestPokerInfoItem(poker));
		}
		return list;
	}
	
	public QuestPokerInfoItem getUnderwayPokerInfo(){
		if(null != this.roleQuestPoker){
			int poker = this.roleQuestPoker.getUnderwayPoker();
			if(poker >= 0){
				return this.buildQuestPokerInfoItem(poker);
			}
		}
		return null;
	}
	
	public void updateRolePokerForAccept(){
		this.roleQuestPoker.setPokerValueFromTemp();
		this.roleQuestPoker.setUpdateTime(new Date());
		if(SaveDbStateType.Insert == this.roleQuestPoker.getSaveDbStateType()){
			GameContext.getBaseDAO().insert(this.roleQuestPoker);
		}else{
			GameContext.getBaseDAO().update(this.roleQuestPoker);
		}
		this.roleQuestPoker.setSaveDbStateType(SaveDbStateType.Initialize);
	}
	
	public void clearTempInfo(){
		this.questLogInfo = null;
		this.roleQuestPoker.clearTempInfo();
	}
	
	public void completeQuest(RoleInstance role){
		//清除任务日志、刷新记录
		this.questLogInfo = null;
		this.roleQuestPoker.clearTempInfo();
		//完成任务
		this.roleQuestPoker.completeQuest(role);
		GameContext.getBaseDAO().update(this.roleQuestPoker);
	}
	
	private QuestPokerInfoItem buildQuestPokerInfoItem(int poker){
		byte[] arrs = this.parsePokerValue(poker);
		QuestPokerInfoItem item = new QuestPokerInfoItem();
		item.setIndex(arrs[0]);
		item.setPokerStylor(arrs[1]);
		item.setPokerNumber(arrs[2]);
		return item;
	}
	
	private byte[] parsePokerValue(int poker){
		byte[] arrs = new byte[3];
		byte index = (byte) (poker / PokerHelper.One_Hundred);
		int pokerValue = poker % PokerHelper.One_Hundred;
		byte stylor = (byte) (pokerValue % PokerHelper.Four);
		byte number = (byte) (pokerValue / PokerHelper.Four);
		arrs[0] = index;
		arrs[1] = stylor;
		arrs[2] = number;
		return arrs;
	}
	
}
