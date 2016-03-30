package com.game.draco.app.union.config;

import lombok.Data;

public @Data class UnionBase {
	
	//公会等级限制
	private short level;

	//物品ID
	private int goodsId;
	
	//物品数量
	private byte goodsNum = 1;
	
	//间隔时间（分钟）
	private int intervalCd;
	
	//弹劾物品类型
	private byte impeachGoodsType;
	
	//弹劾物品ID
	private int impeachGoodsId;
	
	//弹劾物品数量
	private byte impeachGoondsNum;
	
	//几天不上线可弹劾
	private byte impeachDay;
	
	//地图领地ID
	private String mapId;
	
	//传送坐标X （公会领地坐标不在这配置）
	private short transferX;
	
	//传送坐标Y （公会领地坐标不在这配置）
	private short transferY;

    //公会神秘商店Id
    private String unionShopId ;
	
}
