package com.game.draco.app.shop.domain;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.KV;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
/**
 * 
 */
public @Data class ShopGoodsRecord implements KV<Integer,Short>{
	
	@Protobuf(fieldType=FieldType.INT32,order=1)
	private int id;
	@Protobuf(fieldType=FieldType.INT32,order=2)
	private short buyNum;
	
	
	public void addBuyNum(short num){
		buyNum += num;
	}


	@Override
	public Integer $key() {
		return id;
	}


	@Override
	public void $key(Integer value) {
		this.id = value ;
	}


	@Override
	public Short $value() {
		return this.buyNum;
	}


	@Override
	public void $value(Short value) {
		this.buyNum = value ;
	}

}
