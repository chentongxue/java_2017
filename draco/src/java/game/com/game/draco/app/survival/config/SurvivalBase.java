package com.game.draco.app.survival.config;

import java.util.List;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;

import lombok.Data;

public @Data
class SurvivalBase {

	// 地图ID
	private String mapId;

	// 战场人数量
	private int maximum;

	// 获奖次数
	private byte rewardNum;

	// 活动ID
	private short activeId;

	// 玩法名称
	private String baseName;

	// 玩法描述
	private String des;

	// 等待时间
	private byte waitTime;

	// 开启时间
	private String openTime;
	
	//刷新宝箱时间
	private int refBoxTime;

	private byte openHour;
	
	private byte openMinutes;
	
	//进战场定身BUFFID
	private short buffId;

	// 初始化发奖时间
	public void initOpenTime() {
		String[] infos = Util.splitStr(openTime, Cat.colon);
		if (infos.length >= 2) {
			this.openHour = Byte.parseByte(infos[0]);
			this.openMinutes = Byte.parseByte(infos[1]);
		}
	}

	public List<String> getCronExpressionList() {
		if (Util.isEmpty(this.openTime)) {
			return null;
		}
		
		Active active = GameContext.getActiveApp().getActive(activeId);
		if (null == active) {
			return null;
		}
		
		String weekExpress = "? *";
		List<String> list = Lists.newArrayList();
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.openMinutes)
				.append(Cat.blank).append(this.openHour).append(Cat.blank)
				.append(weekExpress).append(Cat.blank).append(active.getWeekTerm());
		list.add(cronExpress.toString());
		return list;
	}
}
