package com.game.draco.app.worldlevel.config;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;

public @Data class WorldLevelStageConfig {

	private String startTime;
	private String endTime;
	private int relativeStartTime;
	private int relativeEndTime;
	private int level;
	
	private Date startDate;
	private Date endDate;
	
	/**
	 * 初始化
	 * @param fileInfo
	 */
	public void init(String fileInfo) {
		DateTimeBean bean = DateConverter.getDateTimeBean(relativeStartTime, relativeEndTime, startTime, endTime, FormatConstant.DEFAULT_YMD);
		if (null == bean) {
			this.checkFail(fileInfo + " startTime=" + this.startTime + " date config error!");
		}
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if (null == this.startDate || null == this.endDate || this.startDate.after(this.endDate)) {
			this.checkFail(fileInfo + " startTime=" + this.startTime + " date config error!");
		}
		if (this.level <= 0) {
			this.checkFail(fileInfo + " startTime=" + this.startTime + " level config error!");
		}
	}
	
	/**
	 * 错误日志
	 * @param info
	 */
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 是否符合条件
	 * @param now
	 * @return
	 */
	public boolean isOnTime(Date now) {
		return DateUtil.dateInRegion(now, this.startDate, this.endDate);
	}
	
}
