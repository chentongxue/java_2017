package com.game.draco.app.inslogic.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 颜色码
 * @author zhouhaobing
 *
 */
public @Data class InsLogicGroup {

	private int duration;
	//地图ID
	private String mapId;
	//精灵ID
	private String spriteId;
	//组ID
	private int groupId;
	
}
