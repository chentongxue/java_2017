package com.game.draco.app.npc.refresh;

import java.util.Calendar;
import java.util.Date;


import lombok.Data;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.vo.Point;

public @Data class NpcRefreshConfig {
	private short id;
	private String npcId;
	private String mapId;
	private int mapX;
	private int mapY;
	/**
	 * 是否随机出生点（地图的路点，如果没有则依然使用mapX,mapY）
	 */
	private boolean randomBornPoint = false ;
	private int _1, _2, _3, _4, _5, _6, _7;
	private String startDate, endDate;
	private int startDateRel, endDateRel;
	//*************************
	//用于boss面板显示
	private byte show ;
	private String timeInfo ;
	private String bossInfo ;
	private String lootId ;
	//*************************
	
	private NpcRefreshRule rule_1 ;
	private NpcRefreshRule rule_2 ;
	private NpcRefreshRule rule_3 ;
	private NpcRefreshRule rule_4 ;
	private NpcRefreshRule rule_5 ;
	private NpcRefreshRule rule_6 ;
	private NpcRefreshRule rule_7 ;
	
	private Date beginDate, overDate;
	
	private byte iconRatio;//BOSS战头像，缩放比

	public Point newBornPoint(){
		Point p = null ;
		if(this.randomBornPoint){
			p = MapUtil.randomCorrectRoadPoint(this.mapId);
		}
		if(null != p){
			return p ;
		}
		return new Point(this.mapId,this.mapX,this.mapY) ;
	}
	
	public boolean init(){
		DateTimeBean bean = DateConverter.getDateTimeBean(this.startDateRel, this.endDateRel, this.startDate, this.endDate, FormatConstant.DEFAULT_YMD);
		if(null == bean){
			return false;
		}
		beginDate = bean.getStartDate();
		overDate = bean.getEndDate();
		return true;
	}
	
	
	//主循环调用的刷新逻辑
	public void update(NpcRefreshTask refresh){
		if(refresh == null){
			return ;
		}
		if(!this.validateRefreshDate()){
			return ;
		}
		
		NpcRefreshRule rule = this.getNpcRefreshRule();
		if(null != rule){
			rule.update(refresh);
		}
	}
	
	
	// 刷新前，怪物喊话
	public void refreshBeforeSpeak(NpcRefreshTask refresh){
		if(refresh == null){
			return ;
		}
		if(!this.validateRefreshDate()){
			return ;
		}
		NpcRefreshRule rule = this.getNpcRefreshRule();
		if(null != rule){
			rule.refreshBeforeSpeak(refresh);
		}
	}

	
	//验证刷新有效日期
	public boolean validateRefreshDate(){
		if(null == this.beginDate && null == this.overDate){
			return  true ;
		}
		Date now = new Date();
		return ((null == this.beginDate) || now.getTime() >= this.beginDate.getTime())
		 && ((null == this.overDate) || now.getTime() <= this.overDate.getTime());
	}
	

	//获取今日刷新规则
	private NpcRefreshRule getNpcRefreshRule(){
		Calendar now = Calendar.getInstance();
		int todayWeek = now.get(Calendar.DAY_OF_WEEK) - 1;
		if(todayWeek == 0){
			todayWeek = 7;
		}
		switch(todayWeek){
			case 1:
				return rule_1;
			case 2:
				return rule_2;
			case 3:
				return rule_3;
			case 4:
				return rule_4;
			case 5:
				return rule_5;
			case 6:
				return rule_6;
			case 7:
				return rule_7;
		}
		return null;
	}
	
	
	
	
	
	/**
	 * 解析时间字符串，格式为（2012-6-13）
	 *//*
	private Date resolveStringDate(String date, int hour, int minute){
		if(Util.isEmpty(date)){
			return null;
		}
		String[] dateTime = date.split(Cat.strigula);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(dateTime[0]));
		calendar.set(Calendar.MONTH, Integer.parseInt(dateTime[1]) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime[2]));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		
		return calendar.getTime();
	}*/
	
}
