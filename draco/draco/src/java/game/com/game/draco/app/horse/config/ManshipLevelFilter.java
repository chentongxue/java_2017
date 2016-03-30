package com.game.draco.app.horse.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * 坐骑骑术等级验证
 * @author zhouhaobing
 *
 */
public @Data class ManshipLevelFilter implements KeySupport<String> {

	//种族
	private byte type;
	//骑术等级
	private int manshipLevel;
	//角色等级
	private short roleLevel;
	
	@Override
	public String getKey(){
		return this.getType() + Cat.underline + this.getManshipLevel();
	}
		
}
