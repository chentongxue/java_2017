package com.game.draco.app.rank.domain;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;

public @Data class RankRewardTime {
	private final static byte REWARD_CYC_DAY = 1;
	private final static byte REWARD_CYC_WEEK = 2;
	private final static byte REWARD_CYC_MONTH = 3;
	private int timeCycleType; //1：每日,2：每周,3：每月
	private int timeFirst; //0-8中的0，每日发奖：0-8 （每天8点）,1-8 （星期天8点）
	private int timeSecond; //0-8中的8
	
	public boolean meetTime(Date curDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		switch(timeCycleType){
		case REWARD_CYC_DAY:
			return timeSecond == hour;
		case REWARD_CYC_WEEK:
			int week = cal.get(Calendar.DAY_OF_WEEK);
			return timeSecond == hour && timeFirst == week;
		case REWARD_CYC_MONTH:
			int month = cal.get(Calendar.DAY_OF_MONTH);
			return timeSecond == hour && timeFirst == month;
		}
		return false;
	}
}
