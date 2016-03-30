package com.game.draco.app.drama.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class DramaQuestTrigger extends DramaTrigger implements KeySupport<String>{
	public final static byte TYPE_ACCEPT = 0;
	public final static byte TYPE_SUBMIT = 1;
	private int questId;
	private byte type; //0:接任务触发1:交任务触发
	
	@Override
	public String getKey() {
		return this.questId + "_" + this.type ;
	}

	
}
