package com.game.draco.app.drama.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class DramaQuestTrigger implements KeySupport<Integer>{
	public final static byte TYPE_ACCEPT = 0;
	public final static byte TYPE_SUBMIT = 1;
	private int questId;
	private short dramaId;
	private String mapId;
	private byte type; //0:接任务触发1:交任务触发
	
	@Override
	public Integer getKey() {
		return this.questId;
	}

	
}
