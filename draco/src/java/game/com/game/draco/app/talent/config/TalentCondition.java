package com.game.draco.app.talent.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class TalentCondition implements KeySupport<Byte>{
	
	//条件ID
	private byte id;
	
	//A范围最小值
	private byte aRangeMin;
	
	//A范围最大值
	private byte aRangeMax;
	
	//B附加值
	private byte addB;
	
	//扣除天赋点
	private int tempTalentNum;

	@Override
	public Byte getKey() {
		return getId();
	}
	
}
