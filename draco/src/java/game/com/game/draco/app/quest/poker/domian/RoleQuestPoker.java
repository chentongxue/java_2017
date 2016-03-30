package com.game.draco.app.quest.poker.domian;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.quest.poker.PokerHelper;

public @Data class RoleQuestPoker {
	
	public static final String ROLEID = "roleId";
	public static final byte DEFAULT_VALUE = -1;
	public static final byte FINISHED_STATUS = 1;
	
	private String roleId;//角色ID
	private int count;//已经使用次数
	private int buyNum ;//购买次数
	private int poker1 = DEFAULT_VALUE;//共3位，从左至右：第一位表示位置，后两位表示扑克
	private int poker2 = DEFAULT_VALUE;
	private int poker3 = DEFAULT_VALUE;
	private byte status;//状态(当前牌的任务是否完成)
	private Date updateTime;//更新时间
	private int tempQuestId;
	private int tempAwardId;
	private int tempPoker = DEFAULT_VALUE;
	
	private SaveDbStateType saveDbStateType = SaveDbStateType.Initialize;
	private boolean modified = false;
	
	/**
	 * 获取当前次数
	 * @return
	 */
	public int getCurrCount(RoleInstance role){
		Date now = new Date();
		if(DateUtil.sameDay(now, this.updateTime)){
			return this.count;
		}
		//数据重置前判断
		GameContext.getQuestPokerApp().onQuestPokerDataReset(role, this);
		this.count = 0;
		this.buyNum = 0 ; //购买次数置为0
		this.updateTime = now;
		this.resetStatus();//重置状态
		this.resetThreePoker();//重置3张牌
		this.clearTempInfo();//清除临时信息
		//放弃任务
		GameContext.getQuestPokerApp().giveUpPokerQuest(role);
		return 0;
	}
	
	private boolean isStatusFinished(){
		return FINISHED_STATUS == this.status;
	}
	
	public boolean isFinishedAll(){
		return this.poker3 > DEFAULT_VALUE && FINISHED_STATUS == this.status;
	}
	
	public void completeQuest(RoleInstance role){
		this.resetStatus();//重置状态
		
		if(!this.isFinishedAll()){
			return ;
		}
		//三张牌全完成，计数+1
		//三张牌的奖励
		GameContext.getQuestPokerApp().sendThreePokerReward(role, this.getPokerValue(this.poker1), 
				this.getPokerValue(this.poker2), this.getPokerValue(this.poker3));
		this.count ++;
		int maxCount = GameContext.getQuestPokerApp().getTotalTime(role);
		//达到最大次数后不清空牌型记录
		if(this.count < maxCount){
			this.resetThreePoker();//重置3张牌
		}
	}
	
	/** 重置状态 **/
	private void resetStatus(){
		this.status = FINISHED_STATUS;
	}
	
	/** 重置3张牌 **/
	public void resetThreePoker(){
		this.poker1 = DEFAULT_VALUE;
		this.poker2 = DEFAULT_VALUE;
		this.poker3 = DEFAULT_VALUE;
	}
	
	/** 清除临时数据 **/
	public void clearTempInfo(){
		this.tempQuestId = 0;
		this.tempAwardId = 0;
		this.tempPoker = DEFAULT_VALUE;
		if(SaveDbStateType.Insert != this.saveDbStateType){
			this.saveDbStateType = SaveDbStateType.Update;
		}
		this.modified = true;
	}
	
	public int getFinishedNumber(){
		if(!this.isStatusFinished()){
			return 0;
		}
		if(this.poker3 > DEFAULT_VALUE){
			return 3;
		}
		if(this.poker2 > DEFAULT_VALUE){
			return 2;
		}
		if(this.poker1 > DEFAULT_VALUE){
			return 1;
		}
		return 0;
	}
	
	public List<Integer> getFinishedPokerList(){
		List<Integer> list = new ArrayList<Integer>();
		if(this.poker3 > DEFAULT_VALUE){
			if(this.isStatusFinished()){
				list.add(this.poker3);
			}
			list.add(this.poker2);
			list.add(this.poker1);
			return list;
		}
		if(this.poker2 > DEFAULT_VALUE){
			if(this.isStatusFinished()){
				list.add(this.poker2);
			}
			list.add(this.poker1);
			return list;
		}
		if(this.poker1 > DEFAULT_VALUE && this.isStatusFinished()){
			list.add(this.poker1);
		}
		return list;
	}
	
	public boolean hasTempQuestPoker(){
		return this.tempAwardId > 0 && this.tempAwardId > 0 && this.tempPoker > DEFAULT_VALUE;
	}
	
	public int getUnderwayPoker(){
		//打开未接取的任务
		if(this.hasTempQuestPoker()){
			return this.tempPoker;
		}
		if(this.isStatusFinished()){
			return DEFAULT_VALUE;
		}
		if(this.poker3 > DEFAULT_VALUE){
			return this.poker3;
		}
		if(this.poker2 > DEFAULT_VALUE){
			return this.poker2;
		}
		return this.poker1;
	}
	
	private int buildPokerValue(byte index, int pokerValue){
		//总共是3位：第一位表示位置，后两位表示扑克
		return index * PokerHelper.One_Hundred + pokerValue;
	}
	
	private int getPokerValue(int poker){
		if(poker > DEFAULT_VALUE){
			return poker % PokerHelper.One_Hundred;
		}
		return DEFAULT_VALUE;
	}
	
	private void setPokerValue(int poker){
		//当前牌完成，给下一张牌赋值；当前未完成，给当前牌赋值。
		if(this.isStatusFinished()){
			this.setNextPokerValue(poker);
		}else{
			this.setCurrPokerValue(poker);
		}
	}
	
	public void setPokerValueFromTemp(){
		this.setPokerValue(this.tempPoker);
	}
	
	private void setCurrPokerValue(int poker){
		if(this.poker3 > DEFAULT_VALUE){
			this.poker3 = poker;
			return;
		}
		if(this.poker2 > DEFAULT_VALUE){
			this.poker2 = poker;
			return;
		}
		this.poker1 = poker;
	}
	
	private void setNextPokerValue(int poker){
		this.status = 0;
		if(this.poker2 > DEFAULT_VALUE){
			this.poker3 = poker;
			return;
		}
		if(this.poker1 > DEFAULT_VALUE){
			this.poker2 = poker;
			return;
		}
		this.poker1 = poker;
	}
	
	public void setTempQuestPoker(int questId, int awardId, byte index, int pokerValue){
		this.tempQuestId = questId;
		this.tempAwardId = awardId;
		this.tempPoker = this.buildPokerValue(index, pokerValue);
		if(SaveDbStateType.Insert != this.saveDbStateType){
			this.saveDbStateType = SaveDbStateType.Update;
		}
		this.modified = true;
	}
	
	private byte getTempIndex(){
		return (byte) (this.tempPoker / PokerHelper.One_Hundred);
	}
	
	public void updateTempQuestPoker(int questId, int awardId, int pokerValue){
		this.tempQuestId = questId;
		this.tempAwardId = awardId;
		this.tempPoker = this.buildPokerValue(this.getTempIndex(), pokerValue);
		if(SaveDbStateType.Insert != this.saveDbStateType){
			this.saveDbStateType = SaveDbStateType.Update;
		}
		this.modified = true;
	}

	public void updateTempQuestId(int questId){
		this.tempQuestId = questId ;
		if(SaveDbStateType.Insert != this.saveDbStateType){
			this.saveDbStateType = SaveDbStateType.Update;
		}
		this.modified = true;
	}
	
	public int getPokerValue1(){
		return this.getPokerValue(this.poker1);
	}
	
	public int getPokerValue2(){
		return this.getPokerValue(this.poker2);
	}
	
	public int getPokerValue3(){
		return this.getPokerValue(this.poker3);
	}
	
}
