package com.game.draco.app.quest.poker.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.quest.poker.PokerTwoType;

public @Data class PokerSecondWeightConfig {
	
	private int twoType;
	private int weight;
	
	public void init(String fileInfo){
		String info = fileInfo + "twoType = " + this.twoType + ",";
		if(null == PokerTwoType.get(this.twoType)){
			this.checkFail(info + "twoType is not exist.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
