package com.game.draco.app.hero.arena.config;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

@Data
public class HeroArenaMatchRuleConfig {
	
	private int groupId;//分组ID
	private int minRatio;//战斗力比率下限
	private int maxRatio;//战斗力比率上限
	private int fetchNum;//抽取人数
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "groupId=" + this.groupId + ",";
		if(this.groupId <= 0){
			this.checkFail(info + "groupId is error.");
		}
		if(this.minRatio < 0){
			this.checkFail(info + "minRatio is error.");
		}
		if(this.maxRatio < 0){
			this.checkFail(info + "maxRatio is error.");
		}
		if(this.fetchNum <= 0){
			this.checkFail(info + "fetchNum is error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
