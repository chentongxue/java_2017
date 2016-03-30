package com.game.draco.app.quest.poker.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.quest.poker.PokerThreeType;
import com.game.draco.app.quest.poker.PokerTwoType;

public @Data class PokerThirdWeightConfig {
	
	private int twoType;//牌型类型
	private int weight0;//杂牌权重
	private int weight1;//对子权重
	private int weight2;//顺子权重
	private int weight3;//同花权重
	private int weight4;//同花顺权重
	private int weight5;//豹子权重
	
	private Map<Integer,Integer> weightMap = new HashMap<Integer,Integer>();
	
	public void init(String fileInfo){
		String info = fileInfo + "twoType = " + this.twoType + ",";
		if(null == PokerTwoType.get(this.twoType)){
			this.checkFail(info + "twoType is not exist.");
		}
		this.weightMap.put(PokerThreeType.Common.getType(), this.weight0);
		this.weightMap.put(PokerThreeType.DuiZi.getType(), this.weight1);
		this.weightMap.put(PokerThreeType.ShunZi.getType(), this.weight2);
		this.weightMap.put(PokerThreeType.TongHua.getType(), this.weight3);
		this.weightMap.put(PokerThreeType.TongHuaShun.getType(), this.weight4);
		this.weightMap.put(PokerThreeType.BaoZi.getType(), this.weight5);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
