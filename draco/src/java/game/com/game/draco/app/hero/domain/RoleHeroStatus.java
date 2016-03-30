package com.game.draco.app.hero.domain;

import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.app.goods.Util;

import com.google.common.collect.Sets;

public @Data class RoleHeroStatus {

	private String roleId ;
	/**
	 * 出战英雄ID
	 */
	private int battleHeroId = 0 ;
	private Set<Integer> switchHeroSet = Sets.newLinkedHashSet();
	private Set<Integer> helpHeroSet = Sets.newLinkedHashSet();
	
	/**
	 * 当前可切换的英雄列表
	 */
	private String switchHeros = "" ;
	/**
	 * 助威英雄id列表
	 */
	private String helpHeros = "" ;
	/**
	 * 上次切换英雄时间
	 */
	private long lastSwitchTime ;
	private boolean inStore = false ;
	
	public boolean deleteHero(int heroId){
		this.helpHeroSet.remove(roleId);
		boolean exist = switchHeroSet.remove(heroId);
		if(battleHeroId != heroId){
			return exist;
		}
		this.battleHeroId = 0 ;
		for(int id : switchHeroSet){
			this.battleHeroId = id ;
			return exist;
		}
		return exist ;
	}
	
	public void postFormStore() {
		this.switchHeroSet.clear();
		this.switchHeroSet.addAll(Util.toIntegerSet(this.switchHeros));
		this.helpHeroSet.clear();
		this.helpHeroSet.addAll(Util.toIntegerSet(this.helpHeros));
	}
	
	public void preToStore(){
		this.switchHeros = Util.toString(this.switchHeroSet);
		this.helpHeros = Util.toString(this.helpHeroSet);
	}
	
	
}
