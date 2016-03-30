package com.game.draco.app.speciallogic.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 特殊数据
 * @author zhouhaobing
 *
 */
public @Data class SpecialLogic implements KeySupport<String> {

	//类型
	private byte type ;
	//地图ID
	private String mapId;
	//npcId
	private String npcId;
	//buffId
	private String buffId;
	
	@Override
	public String getKey(){
		return this.getType() + Cat.underline + getMapId();
	}
	
}
