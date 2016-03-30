package com.game.draco.app.camp.war.vo;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

public @Data class RoleScore {

	private String roleId ;
	/**
	 * 活动结束时角色有可能已经下线
	 */
	private byte campId ;
	private int totalWinTimes ;
	private int totalFailTimes ;
	/**
	 * 最大联胜次数
	 */
	private int maxWinTimes ;
	/**
	 * 活动中获得的游戏币
	 */
	private int gainGameMoney ;
	/**
	 * 活动中获得的声望
	 */
	private int gainPrestige ;
	
	/**
	 * 击杀数
	 */
	private int killNum ;
	/**
	 * 剩余未广播的声望
	 */
	private AtomicInteger remainPrestige = new AtomicInteger(0) ;
}
