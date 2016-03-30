package com.game.draco.app.inslogic.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * 颜色码
 * @author zhouhaobing
 *
 */
public @Data class InsLogic {

	//颜色 （变身-1为不改变）
	private String color ;
	//持续时间
	private int duration;
	//资源ID
	private int resId;
	//缩放-1为不改变
	private int zoom;
	//类型 0颜色 1放大
	private byte type;
	//组ID
	private int groupId;
	
}
