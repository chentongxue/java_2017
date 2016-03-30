package com.game.draco.app.richman.domain;

import java.util.Date;
import java.util.Map;

import sacred.alliance.magic.util.DateUtil;

import lombok.Data;

public @Data class RoleRichMan {
	public final static String ROLE_ID = "roleId" ;
	private int roleId;
	private byte curJoinNum; //当前参加次数
	private int diceNormalNum; //普通掷骰子数
	private byte diceRemoteNum; //遥控骰子数
	private byte diceDoubleNum; //双倍骰子数
	private int todayCoupon; //当天点卷数
	private Date operateDate;
	private int totalCoupon;
	private String randomEventInfo; //随机事件信息 eventId1:step1,eventId2:step2
	
	//标识数据库里面是否有这条记录
	private boolean existRecord;
	//随机事件信息
	private Map<Integer, Byte> randomEventMap = null;
	
	public void reset() {
		Date now = new Date();
		if(null == operateDate) {
			operateDate = now;
			return ;
		}
		if(DateUtil.sameDay(operateDate, now)) {
			return ;
		}
		curJoinNum = 0;
		diceNormalNum = 0;
		diceRemoteNum = 0;
		diceDoubleNum = 0;
		totalCoupon += todayCoupon;
		todayCoupon = 0;
		operateDate = now;
	}

	public void incrJoinNum() {
		this.reset();
		this.curJoinNum++;
	}

	public byte getCurJionNum() {
		this.reset();
		return this.curJoinNum;
	}
	
	public int getTotalCoupon() {
		this.reset();
		return this.totalCoupon;
	}
	
	public byte getDiceRemoteNum() {
		this.reset();
		return this.diceRemoteNum;
	}
	
	public byte getDiceDoubleNum() {
		this.reset();
		return this.diceDoubleNum;
	}
	
	public void incrDiceNormalNum() {
		this.reset();
		this.diceNormalNum++;
	}
	
	public void incrDiceRemoteNum() {
		this.reset();
		this.diceRemoteNum++;
	}
	
	public void incrDiceDoubleNum() {
		this.reset();
		this.diceDoubleNum++;
	}
	
	public int getTodayCoupon() {
		this.reset();
		return this.todayCoupon;
	}
	
	public void setTodayCoupon(int value) {
		this.reset();
		this.todayCoupon = value;
		if(this.todayCoupon < 0) {
			this.todayCoupon = 0;
		}
	}
	
}
