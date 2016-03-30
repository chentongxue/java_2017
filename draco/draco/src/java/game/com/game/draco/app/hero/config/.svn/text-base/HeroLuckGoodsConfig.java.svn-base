package com.game.draco.app.hero.config;

import java.util.Map;

import lombok.Data;

import com.game.draco.GameContext;
import com.google.common.collect.Maps;

public @Data class HeroLuckGoodsConfig {

	private Map<String,HeroLuckGoods> goodsMap = Maps.newHashMap();
	
	public HeroLuckGoods getHeroLuckGoods(int goodsId){
		return goodsMap.get(String.valueOf(goodsId));
	}
	/**
	 * 总权重和
	 */
	private int totalWeights ;
	/**
	 * 普通物品数目
	 */
	private int commonWeightsCount ;
	
	public boolean exist(HeroLuckGoods hlg){
		String key = String.valueOf(hlg.getGoodsId());
		return this.goodsMap.containsKey(key);
	}

	public void add(HeroLuckGoods hlg){
		String key = String.valueOf(hlg.getGoodsId());
		this.goodsMap.put(key, hlg);
	}
	
	public void init(){
		int w = 0 ;
		int c = 0 ;
		for(HeroLuckGoods h : goodsMap.values()){
			w += h.getWeights() ;
			if(1 == h.getWeightsType()){
				c += 1 ;
			}
		}
		this.totalWeights = w ;
		this.commonWeightsCount = c ;
	}
}
