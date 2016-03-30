package com.game.draco.app.union.domain.auction;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public @Data class GoodsItem {
	
	@Protobuf(fieldType=FieldType.STRING,order=1)
	private String goodsInstanceId;
	
	//物品ID
	@Protobuf(fieldType=FieldType.INT32,order=2)
	private int goodsId;
	
	//物品数量
	@Protobuf(fieldType=FieldType.INT32,order=3)
	private byte goodsNum;
	
	//是否绑定
	@Protobuf(fieldType=FieldType.INT32,order=4)
	private byte goodsBinded;
	
}
