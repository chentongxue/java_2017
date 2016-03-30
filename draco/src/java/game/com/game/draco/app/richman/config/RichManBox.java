package com.game.draco.app.richman.config;

import java.util.Map;

import com.google.common.collect.Maps;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class RichManBox implements KeySupport<Integer>{
	private int boxId ; //宝箱id
	private int event1 ; //事件1
	private int weight1 ; //权重1
	private int event2 ; //事件2
	private int weight2 ; //权重2
	private int event3 ; //事件3
	private int weight3 ; //权重3
	private int event4 ; //事件4
	private int weight4 ; //权重4
	private int event5 ; //事件5
	private int weight5 ; //权重5
	private int event6 ; //事件6
	private int weight6 ; //权重6
	private int event7 ; //事件7
	private int weight7 ; //权重7
	private int event8 ; //事件8
	private int weight8 ; //权重8
	private int event9 ; //事件9
	private int weight9 ; //权重9
	private int event10 ; //事件10
	private int weight10; //权重10
	
	//变量
	public Map<Integer, Integer> weightMap = Maps.newHashMap();
	
	@Override
	public Integer getKey() {
		return this.boxId;
	}
	
	public void init() {
		init(this.event1, this.weight1);
		init(this.event2, this.weight2);
		init(this.event3, this.weight3);
		init(this.event4, this.weight4);
		init(this.event5, this.weight5);
		init(this.event6, this.weight6);
		init(this.event7, this.weight7);
		init(this.event8, this.weight8);
		init(this.event9, this.weight9);
		init(this.event10, this.weight10);
	}
	
	public void init(int eventId, int weight) {
		if(eventId <= 0 || weight <= 0) {
			return ;
		}
		this.weightMap.put(eventId, weight);
	}
	
}
