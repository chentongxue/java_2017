package com.game.draco.app.compass.config;

import lombok.Data;

public @Data class CompassRoleAward {
	
	private short id;
	private byte place;
	private int goodsId;
	private int goodsNum;
	private int bindType;
	private String broadcastInfo;//¹ã²¥ÏûÏ¢
	
}
