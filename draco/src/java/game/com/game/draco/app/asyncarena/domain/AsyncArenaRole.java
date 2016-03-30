package com.game.draco.app.asyncarena.domain;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public @Data class AsyncArenaRole {
	
	public final static String ROLE_ID = "roleId" ;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//角色ID
	private int roleId;
	//连胜次数
	private byte successNum;
	//刷新次数
	private byte refNum;
	//挑战次数
	private byte challengeNum;
	//付费次数
	private byte moneyNum;
	//当前荣誉值
	private int nowHonor;
	//历史荣誉值
	private int historyHonor;
	//刷新时间
	private long refTime;
	//对手数据
	private byte [] targetData;
	//昨日排行
	private int historyRanking;
	//是否领奖
	private byte isReward;
	
}
