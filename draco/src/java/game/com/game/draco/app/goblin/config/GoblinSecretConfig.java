package com.game.draco.app.goblin.config;


import java.util.Date;
import java.util.List;

import com.game.draco.app.goblin.GoblinAppImpl;
import com.google.common.collect.Lists;

import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import lombok.Data;

public @Data class GoblinSecretConfig {

	private int startTime;// 难度开始时间（相对）
	private int endTime;// 难度结束时间（相对）
	private String bossId5;// 盗宝哥布林之王
	private String bossId6;// 寻宝哥布林之王
	private String bossId7;// 盗宝矮鬼之王
	private String bossId8;// 盗宝强盗之王
	private String bossId9;// 宝箱BOSS
	
	// -----------------------------------------
	private Date startDate;
	private Date endDate;
	private List<String> goblinList = Lists.newArrayList();// 哥布林BOSS列表
	
	public void init(String fileInfo) {
		String info = fileInfo + "startTime = " + this.startTime + " : ";
		if (this.startTime <= 0) {
			this.checkFail(info + "startTime config error!");
		}
		if (this.endTime < this.startTime) {
			this.checkFail(info + "endTime config error!");
		}
		goblinList.add(this.bossId5);
		goblinList.add(this.bossId6);
		goblinList.add(this.bossId7);
		goblinList.add(this.bossId8);
		this.initTime(fileInfo);
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
	
	/**
	 * 是否是BOSS
	 * @param bossId
	 * @return
	 */
	public boolean isBoss(String bossId) {
		if (Util.isEmpty(bossId)) {
			return false;
		}
		if (bossId.equals(this.bossId9)) {
			return true;
		}
		for (String id : this.goblinList) {
			if (bossId.equals(id)) {
				return true;
			}
		}
		return false;
	}
	
}
