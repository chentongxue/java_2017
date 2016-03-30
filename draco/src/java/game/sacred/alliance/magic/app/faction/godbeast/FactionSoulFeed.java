package sacred.alliance.magic.app.faction.godbeast;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.Util;

public @Data class FactionSoulFeed {
	private int id;//神兽ID
	private int level;//等级
	private int goodsId;//喂养消耗的物品
	private int goodsNum;//喂养消耗的物品数量
	private int growValue;//成长值
	private int contribute;//贡献度
	private int cri1;
	private int weight1;
	private int cri2;
	private int weight2;
	private int cri3;
	private int weight3;
	private int cri4;
	private int weight4;
	private int sumWeight;
	private Map<Integer, Integer> criMap = new HashMap<Integer, Integer>();
	
	public void init(){
		sumWeight = weight1 + weight2 + weight3 + weight4;
		if(weight1 > 0) {
			criMap.put(cri1, weight1);
		}
		
		if(weight2 > 0) {
			criMap.put(cri2, weight2);
		}
		
		if(weight3 > 0) {
			criMap.put(cri3, weight3);
		}
		
		if(weight4 > 0) {
			criMap.put(cri4, weight4);
		}
	}
	
	public Integer getWeightCalct() {
		return Util.getWeightCalct(criMap, sumWeight);
	}
}
