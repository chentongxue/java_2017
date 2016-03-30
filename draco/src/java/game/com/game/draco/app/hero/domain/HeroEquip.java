package com.game.draco.app.hero.domain;

import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.domain.RoleGoods;

import com.google.common.collect.Maps;

/**
 * 
 * 英雄装备
 */
public @Data class HeroEquip {
	private String roleId ;
	/**
	 * 每个英雄的装备列表
	 */
	private Map<Integer,List<RoleGoods>> equipMap = Maps.newHashMap();
}
