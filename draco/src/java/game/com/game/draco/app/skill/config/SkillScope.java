package com.game.draco.app.skill.config;

import lombok.Data;

public @Data
class SkillScope{

	//技能ID
	private short skillId;
	
	//敌方 、自己
	private byte targetXY;
	
	//目标域ID
	private int areaId;
	
	//范围类型（目标、圆、梯形）
	private byte scopeType;

	//半径
	private int radius;
	
	//最大角度
	private int maxDegrees;
	
	//底边长（靠近玩家边）
	private int downLength;
	
	//上边长
	private int upLength;
	
	//高度
	private int hight;
	
	//技能效果目标类型
	private byte effectTarget;
	
	//目标域
	private byte targetScope;
	
	//最大攻击目标数
	private byte targetNum;
	
	//是否补足目标数
	private boolean pentacombo;
	
}
