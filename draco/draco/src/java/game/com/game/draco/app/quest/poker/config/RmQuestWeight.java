package com.game.draco.app.quest.poker.config;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

import lombok.Data;

public @Data class RmQuestWeight {
	
	private int roleLevel;//角色等级
	private int setId1;//任务集合1
	private int weight1;//集合1权重
	private int setId2;//任务集合2
	private int weight2;//集合2权重
	private int setId3;//任务集合3
	private int weight3;//集合3权重
	private int setId4;//任务集合4
	private int weight4;//集合4权重
	private int setId5;//任务集合5
	private int weight5;//集合5权重
	private int setId6;//任务集合6
	private int weight6;//集合6权重
	private int setId7;//任务集合7
	private int weight7;//集合7权重
	
	/** 集合的权重：KEY=集合ID,VALUE=权重 */
	private Map<Integer,Integer> weightMap = new HashMap<Integer,Integer>();
	
	private String loadErrorInfo = "";//加载错误提示信息
	
	public void init(String info){
		this.loadErrorInfo = info + "roleLevel=" + this.roleLevel + ",";
		if(this.roleLevel <= 0){
			Log4jManager.CHECK.error(this.loadErrorInfo);
			Log4jManager.checkFail();
		}
		this.addToSetWeightMap(this.setId1, this.weight1);
		this.addToSetWeightMap(this.setId2, this.weight2);
		this.addToSetWeightMap(this.setId3, this.weight3);
		this.addToSetWeightMap(this.setId4, this.weight4);
		this.addToSetWeightMap(this.setId5, this.weight5);
		this.addToSetWeightMap(this.setId6, this.weight6);
		this.addToSetWeightMap(this.setId7, this.weight7);
	}
	
	private void addToSetWeightMap(int setId, int weight){
		if(setId <= 0){
			return;
		}
		if(weight < 0){
			Log4jManager.CHECK.error(this.loadErrorInfo + "setId=" + setId + ",weight=" + weight);
			Log4jManager.checkFail();
			return;
		}
		this.weightMap.put(setId, weight);
	}
	
	/**
	 * 随机获取任务集合ID
	 * @return
	 */
	public int getRandomSetId(){
		return Util.getWeightCalct(this.weightMap);
	}
	
}
