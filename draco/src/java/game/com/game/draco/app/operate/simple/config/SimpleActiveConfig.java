package com.game.draco.app.operate.simple.config;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.app.operate.vo.OperateActiveBaseConfig;

import lombok.Data;

public @Data class SimpleActiveConfig extends OperateActiveBaseConfig implements KeySupport<Integer> {
	private static final Logger logger = LoggerFactory.getLogger(SimpleActiveConfig.class);
	
	private String activeTitle;
	private String startTime;
	private String endTime;
	private int relativeStartTime;
	private int relativeEndTime;
	
	// 活动开启结束时间
	private Date startDate;
	private Date endDate;
	
	/**
	 * 初始化
	 * @return
	 */
	public Result init() {
		Result result = new Result();
		try {
			DateTimeBean bean = DateConverter.getDateTimeBean(this.relativeStartTime, this.relativeEndTime, this.startTime, this.endTime, FormatConstant.DEFAULT_YMD);
			if (null == bean) {
				result.setInfo("ActiveId is " + this.getActiveId() + " : SimpleActiveConfig date config error!");
				return result;
			}
			this.startDate = bean.getStartDate();
			this.endDate = bean.getEndDate();
			if (null == this.startDate || null == this.endDate || this.startDate.after(this.endDate)) {
				result.setInfo("ActiveId is " + this.getActiveId() + " : SimpleActiveConfig date config error!");
				return result;
			}
			result.success();
		} catch	(Exception e) {
			logger.error("", e);
		}
		return result;
	}
	
	/**
	 * 活动是否开启
	 * @return
	 */
	public boolean isShow() {
		return DateUtil.dateInRegion(new Date(), this.startDate, this.endDate);
	}

	@Override
	public Integer getKey() {
		return this.getActiveId();
	}

}
