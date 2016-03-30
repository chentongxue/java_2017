package com.game.draco.app.union.config.instance;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class UnionInsBoss  implements KeySupport<Byte> {
	
	//活动ID
	private byte activityId;
	
	//组ID
	private String groupId;
	
	//BOSS个数
	private int bossNum;

	
	@Override
	public Byte getKey() {
		return getActivityId();
	}

	
}
