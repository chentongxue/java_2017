package com.game.draco.app.alchemy.vo;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;

import com.game.draco.app.alchemy.config.Alchemy;
/**
 * 炼金结果
 */
public @Data class AlchemyResult extends Result{
	//暴击倍数  0为无暴击
	private byte crit ;
	//获得奖励数值
	private int rewardNum ;
	private Alchemy alchemy ;
	private Map<Byte, Integer> alchemyCountMap ;
	private AttributeType attriType ;
	
}
