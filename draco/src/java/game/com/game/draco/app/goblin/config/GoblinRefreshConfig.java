package com.game.draco.app.goblin.config;

import java.util.Date;
import java.util.List;

import com.game.draco.app.goblin.GoblinAppImpl;
import com.google.common.collect.Lists;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;

public @Data class GoblinRefreshConfig {

	private int startTime;// 难度开始时间（相对）
	private int endTime;// 难度结束时间（相对）
	private String bossId1;// 盗宝哥布林
	private String bossId2;// 寻宝哥布林
	private String bossId3;// 盗宝矮鬼
	private String bossId4;// 盗宝强盗
	
	// ---------------------------------------------
	private Date startDate;
	private Date endDate;
	private List<String> bossList = Lists.newArrayList();
	
	public void init(String fileInfo) {
		String info = fileInfo + "startTime = " + this.startTime + " : ";
		if (this.startTime <= 0) {
			this.checkFail(info + "startTime config error!");
		}
		if (this.endTime < this.startTime) {
			this.checkFail(info + "endTime config error!");
		}
		this.initTime(fileInfo);
		this.initBoss();
	}
	
	private void initTime(String fileInfo) {
		// 获得活动开启的绝对时间
		DateTimeBean bean = DateConverter.getDateTimeBean(this.startTime, this.endTime, GoblinAppImpl.Absolutely_Start_Time, GoblinAppImpl.Absolutely_End_Time, FormatConstant.DEFAULT_YMD);
		if (null == bean) {
			this.checkFail(fileInfo + "activeTime config error!");
			return;
		}
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if (null == this.startDate || null == this.endDate) {
			this.checkFail(fileInfo + "Time config error!");
		}
	}
	
	private void initBoss() {
		this.bossList.add(this.bossId1);
		this.bossList.add(this.bossId2);
		this.bossList.add(this.bossId3);
		this.bossList.add(this.bossId4);
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 是否在这个难度
	 * @param date
	 * @return
	 */
	public boolean isOn(Date date) {
		return DateUtil.dateInRegion(date, this.startDate, this.endDate);
	}
	
}
