package com.game.draco.app.hero.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

import com.game.log.util.Cat;

public @Data class HeroLevelup implements KeySupport<String>{

	private int heroQuality	;
	private int level	;
	private int maxExp	;
	private int maxValorNum ;
	private int maxJusticeNum ;
	
	/**
	 * 升到当前等级需要的总经验
	 * 初始化的时候计算获得
	 */
	private int reachTotalExp ;
	
	@Override
	public String getKey(){
		return heroQuality + Cat.underline + level ;
	}

}
