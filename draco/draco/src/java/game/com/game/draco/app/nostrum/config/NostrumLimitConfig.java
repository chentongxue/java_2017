package com.game.draco.app.nostrum.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

@Data
public class NostrumLimitConfig {
	
	private int goodsId;//秘药ID
	private short minLevel;//等级下限
	private short maxLevel;//等级上限
	private short limitNum;//数量上限
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "goodsId = " + this.goodsId + ", ";
		if(this.minLevel <= 0 || this.maxLevel <= 0){
			this.checkFail(info + "minLevel and maxLevel must be greater than zero.");
		}
		if(this.maxLevel < this.minLevel){
			this.checkFail(info + "maxLevel must be greater than minLevel.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public boolean isSuitLevel(RoleInstance role){
		int level = role.getLevel();
		return level >= this.minLevel && level <= this.maxLevel;
	}
	
}
