package com.game.draco.app.hero.arena.config;

import lombok.Data;

@Data
public class HeroArenaMustRewardGroupConfig {
	
	//关卡ID
	private int groupId;
	private int goodsId;
	private int goodsNum;
	private byte binded;
	
}
