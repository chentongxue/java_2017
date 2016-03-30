package com.game.draco.app.richman.config;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Data;

public @Data class RichManRandomCard {
	private int cardId1 ; //卡片物品id1
	private int weight1 ; //权重1
	private int cardId2 ; //卡片物品id2
	private int weight2 ; //权重2
	private int cardId3 ; //卡片物品id3
	private int weight3 ; //权重3
	private int cardId4 ; //卡片物品id4
	private int weight4 ; //权重4
	
	//变量
	public Map<Integer, Integer> weightMap = Maps.newHashMap();
	
	public void init() {
		init(this.cardId1, this.weight1);
		init(this.cardId2, this.weight2);
		init(this.cardId3, this.weight3);
		init(this.cardId4, this.weight4);
	}
	
	public void init(int eventId, int weight) {
		if(eventId <= 0 || weight <= 0) {
			return ;
		}
		this.weightMap.put(eventId, weight);
	}
}
