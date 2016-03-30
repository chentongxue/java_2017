package com.game.draco.app.goblin.config;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

public @Data class GoblinBaseConfig {

	private String panelStartTime;// 面板显示活动开始时间
	private String panelDesc;// 面板活动描述
	private int secretAccomNum;// 密境容纳人数
	private String secretMapId;// 密境地图ID
	private int minSeries;
	private int maxSeries;

	// ------------------------------------------
	private Date activeStartDate;// 活动开启时间
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private String weekExpress;

	public void init(String fileInfo) {
		if (this.secretAccomNum <= 0) {
			this.checkFail(fileInfo + "secretAccomNum config error!");
		}
		this.initTime(fileInfo);// 处理时间
	}

	private void initTime(String fileInfo) {
		Active active = GameContext.getActiveApp().getOnlyOneActive(ActiveType.Goblin);
		if (null == active) {
			this.checkFail("GoblinApp Active is config error!");
			return;
		}
		this.activeStartDate = active.getStartDate();
		String timeRange1 = active.getTimeRange1();
		if (Util.isEmpty(timeRange1)) {
			this.checkFail("GoblinApp Active timeRange1 is config error!");
			return;
		}
		String[] times = Util.splitStr(timeRange1, Cat.strigula);
		if (times.length < 2) {
			this.checkFail("GoblinApp Active timeRange1 is config error!");
			return;
		}
		String[] startTimeInfo = Util.splitStr(times[0], Cat.colon);
		if (startTimeInfo.length >= 2) {
			this.startHour = Integer.parseInt(startTimeInfo[0]);
			this.startMinute = Integer.parseInt(startTimeInfo[1]);
		}
		String[] endTimeInfo = Util.splitStr(times[1], Cat.colon);
		if (endTimeInfo.length >= 2) {
			this.endHour = Integer.parseInt(endTimeInfo[0]);
			this.endMinute = Integer.parseInt(endTimeInfo[1]);
		}
		// 周执行
		String weekExpress = active.getWeekTerm();
		if (Util.isEmpty(weekExpress)) {
			this.weekExpress = "* * ?";// 每天都执行
			return;
		}
		this.weekExpress = "? * " + weekExpress;
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	/**
	 * 活动现在是否开启
	 * 
	 * @return
	 */
	public boolean isOpenNow() {
		Date now = new Date();
		Date today = DateUtil.getDateZero(now);
		Date tStart = DateUtil.addMinutes(DateUtil.addHours(today, this.startHour), this.startMinute);
		Date tEnd = DateUtil.addMinutes(DateUtil.addHours(today, this.endHour), this.endMinute);
		return DateUtil.dateInRegion(now, tStart, tEnd);
	}

	/**
	 * 获得活动开始时间的定时任务表达式
	 * 
	 * @return
	 */
	public String getStartTimingExpression() {
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.startMinute).append(Cat.blank).append(this.startHour).append(Cat.blank).append(this.weekExpress);
		return cronExpress.toString();
	}

	/**
	 * 获得活动结束的定时任务表达式
	 * 
	 * @return
	 */
	public String getEndTimingExpression() {
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.endMinute).append(Cat.blank).append(this.endHour).append(Cat.blank).append(this.weekExpress);
		return cronExpress.toString();
	}

	/**
	 * 获得随机系数
	 * 
	 * @return
	 */
	public int getRandomSeries() {
		return RandomUtil.randomInt(this.minSeries, this.maxSeries);
	}
	
	public long getGoblinDisappearTime() {
		Date today = DateUtil.getDateZero(new Date());
		Date disDate = DateUtil.addHours(DateUtil.addMinutes(today, this.endMinute), this.endHour);
		return disDate.getTime();
	}

}
