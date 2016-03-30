package com.game.draco.app.horse.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

/**
 * 坐骑基础数据
 * @author zhouhaobing
 *
 */
public @Data class HorseStar implements KeySupport<Byte> {

	//坐骑品质
	private byte quality;
	
	//星级
	private byte star;
	
	//颜色
	private String color;

	@Override
	public Byte getKey(){
		return this.getQuality();
	}
	
}
