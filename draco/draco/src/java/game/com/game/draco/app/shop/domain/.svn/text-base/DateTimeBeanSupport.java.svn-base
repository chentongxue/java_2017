package com.game.draco.app.shop.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.Util;

public @Data class DateTimeBeanSupport {
	
	protected String startDateStr;//绝对售卖开始日期
	protected String endDateStr;//绝对售卖结束日期
	protected int startDay;//相对售卖开始日期（开服的第X天）
	protected int endDay;//相对售卖结束日期（开服的第X天）
	
	protected String startDateStr1;
	protected String endDateStr1;
	protected int startDay1;
	protected int endDay1;
	
	protected String startDateStr2;
	protected String endDateStr2;
	protected int startDay2;
	protected int endDay2;
	
	protected List<DateTimeBean> dateTimeBeanList = new ArrayList<DateTimeBean>();
	
	
	public Result initDateTimeBean(){
		Result result = new Result();
		//清除时间
		dateTimeBeanList.clear();
		//限时的商品，设置构建开始时间与结束时间
		Result timeResult = this.initDateTime(this.startDay, this.endDay, this.startDateStr, this.endDateStr);
		Result timeResult1 = this.initDateTime(this.startDay1, this.endDay1, this.startDateStr1, this.endDateStr1);
		Result timeResult2 = this.initDateTime(this.startDay2, this.endDay2, this.startDateStr2, this.endDateStr2);
		if(!timeResult.isSuccess() || !timeResult1.isSuccess() || !timeResult2.isSuccess()){
			return result.setInfo("The startDate or endDate config error!");
		}
		result.success();
		return result ;
	}

	private Result initDateTime(int startDay0,int endDay0,String startDateStr0,String endDateStr0){
		Result result = new Result();
		if(startDay0 > 0 || endDay0 > 0 || !Util.isEmpty(startDateStr0) || !Util.isEmpty(endDateStr0)){
			DateTimeBean bean = DateConverter.getDateTimeBean(startDay0, endDay0, startDateStr0, endDateStr0, FormatConstant.DEFAULT_YMD);
			if(null == bean){
				return result.setInfo("The startDate or endDate config error!");
			}
			dateTimeBeanList.add(bean);
		}
		result.success();
		return result ;
	}
	
	
	/**
	 * 商品是否可出售
	 * @return
	 */
	public boolean inTime(){
		if(Util.isEmpty(this.dateTimeBeanList)){
			return true ;
		}
		long now = System.currentTimeMillis();
		for(DateTimeBean bean : this.dateTimeBeanList){
			long min =  (null != bean.getStartDate())?bean.getStartDate().getTime():0L ;
			long max =  (null != bean.getEndDate())?bean.getEndDate().getTime():Long.MAX_VALUE ;
			if(now >= min && now <= max){
				return true ;
			}
		}
		return false ;
	}
}
