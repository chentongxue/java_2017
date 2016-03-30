package com.game.draco.app.richman.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class RichManMapEvent implements KeySupport<Byte>{
	private byte gridId; //格子id
	private short posX; //格子x坐标 
	private short posY; //格子y坐标
	private int eventId; //格子上事件id
	
	//变量
	private RichManEvent richManEvent = null;
	
	@Override
	public Byte getKey() {
		return this.gridId;
	}
}
