package com.game.draco.app.hero.domain;

import java.util.Date;

import lombok.Data;

import com.game.log.util.DateUtil;

public @Data class RoleHeroStatus {

	private String roleId ;
	/**
	 * 出战英雄ID
	 */
	private int battleHeroId = 0 ;
	private int todayLuck1Num = 0 ;
	private int todayLuck2Num = 0 ;
	private int todayLuck3Num = 0 ;
	private Date todayLuckDate = new Date();
	/**
	 * 上次免费抽卡时间
	 */
	private Date lastLuck1Date ;
	private Date lastLuck2Date ;
	private Date lastLuck3Date ;
	/**
	 * 付费抽卡次数
	 */
	private int payLuck1Num = 0 ;
	private int payLuck2Num = 0 ;
	private int payLuck3Num = 0 ;
	
	/**
	 * 开启的装备位
	 * 按位操作
	 */
	private short openEquips = 0 ;
	
	private boolean inStore = false ;
	
	public void resetDay(){
		if(null == todayLuckDate){
			todayLuckDate = new Date();
			return ;
		}
		Date now = new Date();
		if(DateUtil.sameDay(now, todayLuckDate)){
			return ;
		}
		this.todayLuck1Num = 0 ;
		this.todayLuck2Num = 0 ;
		this.todayLuck3Num = 0 ;
		this.todayLuckDate = now ;
	}
	
}
