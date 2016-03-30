package com.game.draco.app.drama.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class DramaNpcTrigger extends DramaTrigger implements KeySupport<String>{
	public final static byte TYPE_DIE = 0;
	public final static byte TYPE_BORN = 1;
	private String npcId;
	private byte type; //触发时刻：0：npc死亡触发，1：npc出生触发
	
	public String getKey(){
		return this.npcId + "_" + this.mapId + "_" + type ;
	}
}
