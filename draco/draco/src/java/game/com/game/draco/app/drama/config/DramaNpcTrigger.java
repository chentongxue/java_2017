package com.game.draco.app.drama.config;

import lombok.Data;

public @Data class DramaNpcTrigger{
	public final static byte TYPE_DIE = 0;
	public final static byte TYPE_BORN = 1;
	private String npcId;
	private String mapId;
	private short dramaId;
	private byte type; //触发时刻：0：npc死亡触发，1：npc出生触发
	
}
