package com.game.draco.app.drama.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class DramaNpc implements KeySupport<Short>{
	public final static int ROLE_NPCID = -1; //npcid=-1代表主角
	private short npcId;
	private String npcName;
	private short imageId; //npc头像
	private short resId; //npc形象
	private byte resType; //0:npc, 1:角色
	
	@Override
	public Short getKey() {
		return this.npcId;
	}
}
