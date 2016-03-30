package com.game.draco.app.quest.poker.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class RmQuestAwardWeight {
	
	private int roleLevel;//角色等级
	private int awardId1;//奖励ID1
	private int weight1;//权重1
	private int awardId2;//奖励ID2
	private int weight2;//权重2
	private int awardId3;//奖励ID3
	private int weight3;//权重3
	private int awardId4;//奖励ID4
	private int weight4;//权重4
	private int awardId5;//奖励ID5
	private int weight5;//权重5
	
	/** 奖励的权重：KEY=奖励ID,VALUE=权重 */
	private Map<Integer,Integer> weightMap = new HashMap<Integer,Integer>();
	
	private String loadErrorInfo = "";//加载错误提示信息
	
	public void init(String fileInfo){
		this.addToAwardWeightMap(this.awardId1, this.weight1);
		this.addToAwardWeightMap(this.awardId2, this.weight2);
		this.addToAwardWeightMap(this.awardId3, this.weight3);
		this.addToAwardWeightMap(this.awardId4, this.weight4);
		this.addToAwardWeightMap(this.awardId5, this.weight5);
		this.loadErrorInfo = fileInfo + "roleLevel=" + this.roleLevel + ".";
		if(this.roleLevel <= 0){
			this.checkFail(this.loadErrorInfo);
		}
		if(0 == this.weightMap.size()){
			this.checkFail(this.loadErrorInfo + "weightMap is empty!");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	private void addToAwardWeightMap(int awardId, int weight){
		//没有配置，不需要关心
		if(awardId <= 0){
			return;
		}
		if(weight <= 0){
			this.checkFail(this.loadErrorInfo + "awardId=" + awardId + ",weight=" + weight);
			return;
		}
		this.weightMap.put(awardId, weight);
	}
	
	/**
	 * 随机获取奖励集合ID
	 * @return
	 */
	public int getRandomAwardId(){
		return Util.getWeightCalct(this.weightMap);
	}
	
}
