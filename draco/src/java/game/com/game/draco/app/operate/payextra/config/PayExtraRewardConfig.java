package com.game.draco.app.operate.payextra.config;

import java.util.Date;

import com.game.draco.app.operate.payextra.PayExtraType;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

public @Data class PayExtraRewardConfig implements KeySupport<Integer> {
	
	private int rechargePoint;// 充值钻石
	private int rewardPoint;// 赠送钻石
	private String desc;// 充值面板描述
	private String startTime;// 重置开始时间（绝对）格式：2014-10-1
	private String endTime;// 重置结束时间（绝对）格式：2014-10-1
	private int recommend;// 是否推荐
	private byte extraType;// 类型
	private int activeId;// 对应活动Id
	
	// 非配置
	private Date startDate;
	private Date endDate;
	
	public Result init() {
		String info = "PayExtraResetConfig startTime : " + this.startTime + ".";
		Result result = new Result();
		PayExtraType extraType = PayExtraType.get(this.extraType);
		if (null == extraType) {
			result.setInfo(info + "extraType not exists");
			return result;
		}
		// 允许不配置重置时间
		if (Util.isEmpty(this.startTime) || Util.isEmpty(this.endTime)) {
			return result.success();
		}
		// 处理重置时间
		DateTimeBean bean = DateConverter.getDateTimeBean(0, 0, this.startTime, this.endTime, FormatConstant.DEFAULT_YMD);
		if (null == bean) {
			return result.setInfo(info + "startTime, endTime config error!");
		}
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if (null == this.startDate || null == this.endDate) {
			return result.setInfo(info + "Please config the startTime or the endTime.");
		}
		if(this.startDate.after(this.endDate)){
			return result.setInfo(info + "startTime, endTime config error!");
		}
		return result.success();
	}

	@Override
	public Integer getKey() {
		return this.rechargePoint;
	}
	
	/**
	 * 当前是否处于重置活动内
	 * @return
	 */
	public boolean isReset() {
		if (null == startDate || null == endDate) {
			return false;
		}
		return DateUtil.dateInRegion(new Date(), startDate, endDate);
	}
	
}
