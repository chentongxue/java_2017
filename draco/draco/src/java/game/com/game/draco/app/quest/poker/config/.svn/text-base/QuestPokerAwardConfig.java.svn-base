package com.game.draco.app.quest.poker.config;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class QuestPokerAwardConfig {
	
	private int roleLevel;//角色等级
	private int exp;//经验
	
	public void init(String fileInfo){
		String info = fileInfo + "roleLevel = " + this.roleLevel + ",";
		if(this.roleLevel <= 0){
			this.checkFail(info + "roleLevel is error.");
		}
		if(this.exp <= 0){
			this.checkFail(info + "exp is error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
