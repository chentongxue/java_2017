package sacred.alliance.magic.app.summon;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

public @Data class SummonGroup implements KeySupport<Integer> {
	
	private int groupId;
	private int ruleId1;
	private int weight1;
	private int ruleId2;
	private int weight2;
	private int ruleId3;
	private int weight3;
	private int ruleId4;
	private int weight4;
	private int ruleId5;
	private int weight5;
	private int ruleId6;
	private int weight6;
	private int ruleId7;
	private int weight7;
	private int ruleId8;
	private int weight8;
	private int ruleId9;
	private int weight9;
	private int ruleId10;
	private int weight10;
	private int ruleId11;
	private int weight11;
	private int ruleId12;
	private int weight12;
	private int ruleId13;
	private int weight13;
	
	private Map<Integer, Integer> groupMap = new HashMap<Integer, Integer>();
	private int sumWeight;
	
	public void init(){
		sumWeight = 0 ;
		groupMap.clear();
		this.initWeight(ruleId1, weight1);
		this.initWeight(ruleId2, weight2);
		this.initWeight(ruleId3, weight3);
		this.initWeight(ruleId4, weight4);
		this.initWeight(ruleId5, weight5);
		this.initWeight(ruleId6, weight6);
		this.initWeight(ruleId7, weight7);
		this.initWeight(ruleId8, weight8);
		this.initWeight(ruleId9, weight9);
		this.initWeight(ruleId10, weight10);
		this.initWeight(ruleId11, weight11);
		this.initWeight(ruleId12, weight12);
		this.initWeight(ruleId13, weight13);
		for(int w : this.groupMap.values()){
			sumWeight += w ;
		}
	}
	
	private void initWeight(int ruleId,int weight){
		if(weight <= 0){
			return ;
		}
		groupMap.put(ruleId, weight);
	}
	
	
	public Integer getWeightRuleId() {
		return Util.getWeightCalct(groupMap, sumWeight);
	}
	
	@Override
	public Integer getKey() {
		return this.getGroupId();
	}
}
