package sacred.alliance.magic.app.arena.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

public @Data class ArenaMapConfig implements KeySupport<Integer>{

	private int arenaType ;
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
	private int sumWeight;
	private Map<Integer, Integer> groupMap = new HashMap<Integer, Integer>();
	
	public void init(){
		sumWeight = weight1 + weight2 + weight3 + weight4 + weight5 ;
		if(weight1 > 0) {
			groupMap.put(ruleId1, weight1);
		}
		
		if(weight2 > 0) {
			groupMap.put(ruleId2, weight2);
		}
		
		if(weight3 > 0) {
			groupMap.put(ruleId3, weight3);
		}
		
		if(weight4 > 0) {
			groupMap.put(ruleId4, weight4);
		}
		
		if(weight5 > 0) {
			groupMap.put(ruleId5, weight5);
		}
	}
	
	public Integer getWeightRuleId() {
		return Util.getWeightCalct(groupMap, sumWeight);
	}
	
	@Override
	public Integer getKey() {
		return this.arenaType;
	} 
}
