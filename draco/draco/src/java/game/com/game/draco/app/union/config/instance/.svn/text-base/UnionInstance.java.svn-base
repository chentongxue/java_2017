package com.game.draco.app.union.config.instance;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionInstance  implements KeySupport<Byte> {
	
	//活动ID
	private byte activityId;
	
	//副本名称
	private String name;
	
	//地图ID
	private String mapId;
	
	//玩家位置X
	private short mapX;
	
	//玩家位置Y
	private short mapY;
	
	//dps排行奖励组ID
	private byte rankGroupId;
	
	//类型 0单一击杀 1全部击杀 2随机击杀一个
	private byte type;
	
	@Override
	public Byte getKey() {
		return getActivityId();
	}

	
}
