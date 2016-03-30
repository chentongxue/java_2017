package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class HeroNumRoleLevel implements KeySupport<Integer>{

	private int roleLevel ;
	private byte heroNum ;
	//助威的英雄数目
	private byte helpHeroNum ;
	
	public Integer getKey(){
		return this.roleLevel ;
	}
}
