package com.game.draco.app.copy.config;

import sacred.alliance.magic.vo.Point;
import lombok.Data;

public @Data class CopyBaseConfig {
	
	private String mapId;// 容错点地图
	private int maxHeroCopyEnter;// 默认英雄副本最大进入次数
	private int maxTeamCopyEnter;// 默认组队副本最大进入次数
	private int openRaidsLevel;// 开启扫荡等级
	private int buyHeroCopyNum;// 默认可以购买副本次数
	private int prestigeLevel;// 可获得威望等级差
	// --------------------------------------------------
	private Point failurePoint;// 出副本不可传回原点时，传送到此点

}
