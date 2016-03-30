package com.game.draco.app.horse.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * 坐骑种族骑术描述
 * @author zhouhaobing
 *
 */
public @Data class HorseRace implements KeySupport<Byte> {

	//种族类型
	private byte raceType;
	//种族名称
	private String raceName;
	//骑术名称
	private String manshipName;
	
	@Override
	public Byte getKey(){
		return this.getRaceType();
	}	
}
