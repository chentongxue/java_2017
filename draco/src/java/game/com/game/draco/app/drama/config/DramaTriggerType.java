package com.game.draco.app.drama.config;

public enum DramaTriggerType {
	Point((byte)1),//点触发
	EntryMap((byte)2),//进入地图触发
	AcceptQuest((byte)3),//接受任务
	SubmitQuest((byte)4),//交任务
	NpcDie((byte)5),//npc死亡触发
	NpcBorn((byte)6),//npc出生触发
	RoleDie((byte)7),//角色死亡
	;
	
	private byte type;
	
	DramaTriggerType(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return this.type;
	}
	
}
