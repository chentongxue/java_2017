package com.game.draco.app.unionbattle.config;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.unionbattle.type.IntegralBattleWeekType;
import com.google.common.collect.Lists;

public @Data class UnionIntegral{
	
	//活动ID
	private short activeId;
	
	//地图ID
	private String mapId;
	
	//每轮间隔时间
	private int intervalTime;
	
	//战斗时间
	private int battleTime;
	
	//奖励物品ID
	private String rewGoodsId;
	
	//场次
	private byte round;
	
	//活动说明
	private String details;
	
	// 开启时间
	private String openTime;
	
	private List<Date> startDateList;
	
	private List<Date> endDateList;
	
	//星期几发奖
	private byte awardWeek;
	
	//发奖时间
	private String awardTime;

	private byte awardHour;
	
	private byte awardMinutes;
	
	//清一周记录时间
	private byte clearBattle;
	
	//参加数量
	private int joinNum;
	
	//创建对战时间
	private String createFightTime;
	
	private byte createFightHour;
	
	private byte createFightMinutes;
	
	//开箱子数量
	private int openNum;
	
	/**
	 * 初始化活动开启时间
	 */
	public void initOpenTime() {
		String[] infos = Util.splitStr(openTime, Cat.colon);
		if (infos.length >= 2) {
			initStartDateList();
			initEndDateList();
		}
	}
	
	private void initEndDateList() {
		endDateList = Lists.newArrayList();
		for(Date dateTime : getStartDateList()){
			Date endDate = DateUtil.addMinutes(dateTime,getIntervalTime()/60);
			endDateList.add(endDate);
		}	
	}

	/**
	 * 初始化发奖时间
	 */
	public void initAwardTime() {
		String[] infos = Util.splitStr(awardTime, Cat.colon);
		if (infos.length >= 2) {
			this.awardHour = Byte.parseByte(infos[0]);
			this.awardMinutes = Byte.parseByte(infos[1]);
		}
	}
	
	/**
	 * 初始化创建对战列表时间
	 */
	public void initCreateFightTime() {
		String[] infos = Util.splitStr(createFightTime, Cat.colon);
		if (infos.length >= 2) {
			this.createFightHour = Byte.parseByte(infos[0]);
			this.createFightMinutes = Byte.parseByte(infos[1]);
		}
	}

	/**
	 * 活动开启时间
	 * @return
	 */
	public List<String> getOpenTimeCronExpressionList() {
		if (Util.isEmpty(this.openTime)) {
			return null;
		}
		
		Active active = GameContext.getActiveApp().getActive(activeId);
		if (null == active) {
			return null;
		}
		
		String weekExpress = "? *";
		List<String> list = Lists.newArrayList();
		
		List<Date> startDateList = getStartDateList();
		
		for(int i=0;i<round;i++){
			StringBuffer cronExpress = new StringBuffer();
			Date startDate = startDateList.get(i);
			cronExpress.append("0").append(Cat.blank).append(DateUtil.getMinutes(startDate))
			.append(Cat.blank).append(DateUtil.getHour(startDate)).append(Cat.blank)
			.append(weekExpress).append(Cat.blank).append(active.getWeekTerm());
			list.add(cronExpress.toString());
		}		
		return list;
	}
	
	private void initStartDateList(){
		List<Integer> minutesList = Lists.newArrayList();
		int min = 0;
		for(int i=0;i<getRound();i++){
			if(i==0){
				minutesList.add(0);
				continue;
			}
			min += getIntervalTime();
			minutesList.add(min/60);
		}	
		
		String [] minutesArr = getOpenTime().split(Cat.colon);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(minutesArr[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(minutesArr[1]));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		startDateList = Lists.newArrayList();
		for(Integer minu : minutesList){
			Date startDate = DateUtil.addMinutes(cal.getTime(),minu);
			startDateList.add(startDate);
		}	
	}
	
	/**
	 * 发奖时间
	 * @return
	 */
	public List<String> getAwardTimeCronExpressionList() {
		if (Util.isEmpty(this.awardTime)) {
			return null;
		}
		String weekExpress = "? *";// 周几执行
		
		List<String> list = Lists.newArrayList();
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.awardMinutes)
				.append(Cat.blank).append(this.awardHour).append(Cat.blank)
				.append(weekExpress).append(Cat.blank).append(IntegralBattleWeekType.get(awardWeek).name());
		list.add(cronExpress.toString());
		return list;
	}
	
	/**
	 * 清一周记录时间
	 * @return
	 */
	public List<String> getClearTimeCronExpressionList() {
		if(clearBattle == 0){
			return null;
		}
		String weekExpress = "0 1 1 ? *";
		List<String> list = Lists.newArrayList();
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append(weekExpress).append(Cat.blank).append(IntegralBattleWeekType.get(clearBattle).name());
		list.add(cronExpress.toString());
		return list;
	}
	
	/**
	 * 创建对战列表
	 * @return
	 */
	public List<String> getCreateFightTimeCronExpressionList() {
		if (Util.isEmpty(this.createFightTime)) {
			return null;
		}
		
		Active active = GameContext.getActiveApp().getActive(activeId);
		if (null == active) {
			return null;
		}
		String weekExpress = "? *";// 周几执行
		
		List<String> list = Lists.newArrayList();
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.createFightMinutes)
				.append(Cat.blank).append(this.createFightHour).append(Cat.blank)
				.append(weekExpress).append(Cat.blank).append(active.getWeekTerm());
		list.add(cronExpress.toString());
		return list;
	}
	
}
