package com.game.draco.app.compass.domain;

import lombok.Data;
/**
 * 罗盘奖励物品信息
 * @author gaibaoning@moogame.cn
 * @date 2014-3-28 上午10:10:31
 */
public @Data class CompassRoleAward {
	
	private short id;           //罗盘ＩＤ
	private byte place;
	private int goodsId;        //奖品ID
	private int goodsNum;        //奖品数量
	private int bindType;        //绑定类型
	private String broadcastInfo;//奖励广播信息
	
}
