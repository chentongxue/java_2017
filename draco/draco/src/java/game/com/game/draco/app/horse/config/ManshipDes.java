package com.game.draco.app.horse.config;

import lombok.Data;

/**
 * 坐骑骑术描述
 * @author zhouhaobing
 *
 */
public @Data class ManshipDes {

	//最小等级
	private short minLevel;
	//最大等级
	private short maxLevel;
	//描述
	private String des;
	
		
}
