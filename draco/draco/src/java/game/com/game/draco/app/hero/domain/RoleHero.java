package com.game.draco.app.hero.domain;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.GameContext;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.google.common.collect.Maps;

public @Data class RoleHero {
	
	public final static String ROLE_ID = "roleId" ;
	public final static String HERO_ID = "heroId" ;
	private int heroId ;
	private String roleId ;
	private int level  = 1;
	private int exp ;
	/**
	 * 勇气印记
	 */
	private int valorNum ;
	/**
	 * 正义印记
	 */
	private int justiceNum ;
	
	private byte quality ;
	
	private byte star ;
	/**
	 * 品进度
	 */
	private int qualityProgress ;
	
	/**
	 * 英雄技能str串
	 * id:lv,id:lv
	 */
	@JSONField(serialize=false)
	private String skills = "" ;
	
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
	
	/**
	 * 获取英雄的战斗力
	 * @return
	 */
	public int getBattleScore(){
		AttriBuffer buffer = GameContext.getHeroApp().getHeroAttriBuffer(this);
		return GameContext.getAttriApp().getAttriBattleScore(buffer);
	}
	
}
