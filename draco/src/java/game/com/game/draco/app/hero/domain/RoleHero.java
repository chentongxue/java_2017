package com.game.draco.app.hero.domain;

import java.util.Map;

import lombok.Data;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.base.QualityStarSupport;
import com.google.common.collect.Maps;

public @Data class RoleHero implements QualityStarSupport{
	
	public final static String ROLE_ID = "roleId" ;
	public final static String HERO_ID = "heroId" ;
	public final static short HP_RATE_FULL = 10000 ;
	public static final short HP_ADD_RATIO = 2000;
	private int heroId ;
	private String roleId ;
	private int level  = 1;
	private int exp ;
	private byte quality ;
	
	private byte star ;
	/**
	 * 品进度
	 */
	private int qualityProgress ;
	/**
	 * hp百分比
	 * 100%=10000 
	 */
	private short hpRate = -1 ;
	
	/**
	 * 英雄技能str串
	 * id:lv,id:lv
	 */
	private String skills = "" ;
	/**
	 * 战斗力
	 */
	private int score  = 0 ;
	
	public void setHpRate(short hpRate){
		if(hpRate == this.hpRate){
			return ;
		}
		this.hpRate = hpRate ;
		this.setModify(true);
	}
	
	public void setScore(int score){
		if(score == this.score){
			return ;
		}
		this.score = score ;
		this.setModify(true);
	}
	
	///////////////////////////////////////
	// 下面字段为非数据库字段
	///////////////////////////////////////
	
	/**
	 * 存储技能,从skills 而来
	 */
	@JSONField(serialize=false)
	private Map<Short,RoleSkillStat> skillMap = Maps.newHashMap();
	
	@JSONField(serialize=false)
	private byte onBattle ;
	
	@JSONField(serialize=false)
	private boolean modify = false ;
	
	@JSONField(serialize=false)
	private boolean fromSystem = false ;
	
	public short getHpRate(){
		if(hpRate < 0 || hpRate > HP_RATE_FULL){
			hpRate = HP_RATE_FULL ;
		}
		return hpRate ;
	}
	
}
